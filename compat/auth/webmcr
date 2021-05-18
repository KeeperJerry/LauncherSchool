<?php
#
# Скрипт Авторизации webmcr
#
# Оригинал https://github.com/microwin7/Gravit-Request
#
# Допилил https://github.com/Divarion-D
#
header("Content-Type: text/plain; charset=UTF-8");
if (config::$settings['tech_work'] == true) {
    die(messages::$msg['tech_work']);
}

$login = '';
$pass = '';

if(isset($_GET['login'])) {
    $login = str_replace(' ', '', $_GET['login']);
}
if(isset($_GET['password'])) {
    $pass = $_GET['password'];
}

class config
{
    static $settings = array(
        "db_host" => 'localhost', // 127.0.0.1 или localhost или IP
        "db_port" => '3306', // порт к БД
        "db_user" => '', // Имя пользователя БД
        "db_pass" => '', // Пароль БД
        "db_db" => '', // Имя базы данных сайта
        "un_tpl" => '([a-zA-Z0-9\_\-]+)', // Проверка на Regexp
        "debug_mysql" => false, // Проверка на ошибки MySQL. Сохранение в файл debug.log !!! Не устанавливайте true навсегда и не забудьте после настройки удалить файл debug.log из папки
        "tech_work" => false //Техработы
    );
    //Настройка названия таблицы, колонок и permission
    static $table = array(
        // WebMCR
        "wmcr_tn" => "mcr_users", // Название таблици
        "wmcr_user" => "login", // Название колонки пользователя
        "wmcr_email" => "email", // Название колонки email
        "wmcr_pass" => "password", // Название колонки password
    );
    public static $mainDB = null;
    public static function initMainDB()
    {
        if (config::$mainDB == null)
            config::$mainDB = new db('', 0, true);
    }
}

class messages
{
    static $msg = array(
        "err" => "Ошибка ",
        "player_not_found" => "Пользователь не найден",
        "pass_not_found" => "Пароль не найден",
        "incorrect_pass" => "Пароль неверный",
        "tech_work" => "Проводятся тех. работы",
        "rgx_err" => "Проверка на Regexp выявила несоответствие",
        "player_null" => "Пользователь не может быть пустым",
        "pass_null" => "Пароль не может быть пустым",
        "php_old" => "Используйте версию PHP 5.6 и выше. "
    );
}
if (strnatcmp(phpversion(), '5.6') >= 0) {
    if (exists($login)) {
            if (rgxp_valid($login)) {
                if (exists($pass)) {
                    auth($login);
                } else {
                    die(messages::$msg['pass_null']);
                }
            }
    } else {
        die(messages::$msg['player_null']);
    }
} else {
    echo messages::$msg['php_old'];
    die("Ваша версия → " . phpversion());
}
function rgxp_valid($var)
{
    if (preg_match("/^" . config::$settings['un_tpl'] . "/", $var, $varR) == 1 || filter_var($var, FILTER_VALIDATE_EMAIL)) {
        return true;
    } else {
        die(messages::$msg['rgx_err']);
    }
}
function auth($login)
{
    config::initMainDB();
    $tn = config::$table['wmcr_tn'];
    $cl_user = config::$table['wmcr_user'];
    $email = config::$table['wmcr_email'];
    $password = config::$table['wmcr_pass'];
    $qr = config::$mainDB->query("SELECT `" . $cl_user . "`,$password" . ",salt FROM " . $tn . " WHERE ($email=? OR `" . $cl_user . "`=?) LIMIT 1", "ss", $login, $login)->fetch_assoc();
    if (!isset($qr[$password]) && !isset($qr[$cl_user])) {
        die(messages::$msg['player_not_found']);
    }
    $user = $qr[$cl_user];
    
    pass_valid($user, $qr[$password], $qr['salt']);
}
function pass_valid($user, $pass_check, $salt)
{
    global $pass;
    $i = 1;
    $generations = array(
        sha1($pass),
        hash('sha256', $pass),
        hash('sha512', $pass),
        md5(md5($pass)),
        md5($pass.$salt), // Joomla
        md5($salt.$pass), // osCommerce, TBDev
        md5(md5($salt).$pass), // vBulletin, IceBB, Discuz
        md5(md5($pass).$salt),
        md5($pass.md5($salt)),
        md5($salt.md5($pass)),
        sha1($pass.$salt),
        sha1($salt.$pass),
        md5(md5($salt).md5($pass)), // ipb, MyBB
        hash('sha256', $pass.$salt),
        hash('sha512', $pass.$salt),
        md5($pass)
    );
    
    $genmodels = count($generations);
    
    foreach ($generations as $gen) {
        $log = date('Y-m-d H:i:s') . $gen.' '.$pass_check;
        file_put_contents(__DIR__ . '/log.txt', $log . PHP_EOL, FILE_APPEND);
        if ($gen === $pass_check) {
            echo 'OK:' . $user;
            exit;
        } else {
            if ($i == $genmodels){
                die(messages::$msg['incorrect_pass']);
            }
        }
        $i++;
    }
}
function exists($var)
{
    if (!empty($var) && isset($var)) return true;
    else return false;
}
class db
{
    private $mysqli;
    private $last;
    public function __construct($srv = '', $number = 0, $isMain = false)
    {
        if ($isMain) {
            $config = config::$settings;
            $this->mysqli = new mysqli($config['db_host'], $config['db_user'], $config['db_pass'], $config['db_db'], $config['db_port']);
        }
        if ($this->mysqli->connect_errno) {
            $this->debug("Connect error: " . $this->mysqli->connect_error);
        }
        $this->mysqli->set_charset("utf8");
    }
    public function __destruct()
    {
        $this->close();
    }
    public function close()
    {
        if (!is_null($this->mysqli)) {
            $this->mysqli->close();
        }
    }
    function refValues($arr)
    {
        $refs = array();
        foreach ($arr as $key => $value) {
            $refs[$key] = &$arr[$key];
        }
        return $refs;
    }
    private function argsToString($args)
    {
        if (count($args) == 0)
            return "";
        $str = $args[0] . "";
        for ($i = 1; $i < count($args); ++$i) {
            $str .= ", " . $args[$i];
        }
        return $str;
    }
    public function query($sql, $form = "", ...$args)
    {
        $this->debug(" Executing query " . $sql . " with params: $form ->" . $this->argsToString($args));
        $stmt = $this->mysqli->prepare($sql);
        if ($this->mysqli->errno) {
            $this->debug('Statement preparing error[1]: ' . $this->mysqli->error . " ($sql)");
            exit();
        }
        array_unshift($args, $form);
        if ($form != "") {
            call_user_func_array(array($stmt, "bind_param"), $this->refValues($args));
        }
        $stmt->execute();
        if ($stmt->errno) {
            $this->debug("Statement execution error: " . $stmt->error . "($sql)");
            exit();
        }
        $this->last = $stmt->get_result();
        $stmt->close();
        return $this->last;
    }
    public function assoc()
    {
        if ($this->last === null) {
            return null;
        }
        return $this->last->fetch_assoc();
    }
    public function all()
    {
        if ($this->last === null) {
            return null;
        }
        return $this->last->fetch_all();
    }
    public function debug($message)
    {
        if (config::$settings['debug_mysql']) {
            file_put_contents("debug.log", date('d.m.Y H:i:s - ') . $message . "\n", FILE_APPEND);
        }
    }
}
