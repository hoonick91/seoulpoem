const mysql = require('promise-mysql');
const dbConfig = {
  host : 'neo20.csejtv0kd2sq.ap-northeast-2.rds.amazonaws.com',
  port : 3306,
  user : 'jungin641',
  password : '1311wjddls!',
  database : 'managerNa_aj',
  connectionLimit : 43,
  waitForConnection : true
};
const db_pool = mysql.createPool(dbConfig);
module.exports = db_pool;
