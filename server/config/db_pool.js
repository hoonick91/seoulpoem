const mysql = require('promise-mysql');
const dbConfig = {
  host : 'neo20.ckxkmqdzg9z9.ap-northeast-2.rds.amazonaws.com',
  port : 3306,
  user : 'csr1994',
  password : 'passpass',
  database : 'seoul_peom',
  connectionLimit : 43,
  waitForConnection : true
};
const db_pool = mysql.createPool(dbConfig);
module.exports = db_pool;
