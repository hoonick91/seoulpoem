const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');

// 작가 신청하기
router.post('/', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();


      //users_idusers 수정할것!
      let query1 = 'select idusers from seoul_poem.users where idusers = 1 and author = 1';
      let data = await connection.query(query1);
      if(data.length>0) res.status(403).send({result: '이미 작가 신청을 했습니다'});

      //users_idusers 수정할것!
      let query2 = 'update seoul_poem.users set author=1 where idusers=1';
      await connection.query(query2);


      res.status(201).send({result: "author apply success"});
      await connection.commit();

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// 작가 목록 조회
router.get('/', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let query1 = 'SELECT count(idusers) from seoul_poem.users where author = 1';
      var count_authors = await connection.query(query1);

      let query2 = 'SELECT idusers, pen_name, profile, inform from seoul_poem.users where author=1';
      var authors_list = await connection.query(query2);

      res.status(200).send({count_authors: count_authors, authors_list: authors_list});
      await connection.commit();

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});


module.exports = router;
