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

      let email =req.headers.email;

      let query1 = 'select email from seoul_poem.users where email = ? and author = 1';
      let data = await connection.query(query1, email);
      if(data.length>0) res.status(403).send({result: '이미 작가 신청을 했습니다'});

  
      let query2 = 'update seoul_poem.users set author=1 where email = ?';
      await connection.query(query2, email);


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

      let query1 = 'SELECT count(*) from seoul_poem.users where author = 1';
      var count_authors = await connection.query(query1);

      let query2 = 'SELECT email, pen_name, profile, inform from seoul_poem.users where author=1';
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
