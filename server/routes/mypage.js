const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');
const s3 = new aws.S3();

const upload = multer({
  storage: multerS3({
    s3: s3,
    bucket: 'csr1994',
    acl: 'public-read',
    key: function (req, file, cb) {
      cb(null, Date.now() + '.' + file.originalname.split('.').pop());
    }
  })
});


// 프로필 정보 조회
router.post('/', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      //idusers 수정할것!
      let query1 = 'select * from seoul_poem.users where idusers = 1';
      let mypage = await connection.query(query1, req.params.idarticles);

      res.status(201).send({mypage : mypage});
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

// 사용자의 그림을 조회
router.post('/photo', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      //idusers 수정할것!
      let query1 = 'SELECT idarticles, pictures_idpictures, photo FROM seoul_poem.users, seoul_poem.articles, seoul_poem.pictures where users.idusers = articles.users_idusers and articles.pictures_idpictures = pictures.idpictures and idusers=1';
      let myphoto = await connection.query(query1);

      let query2 = 'SELECT count(idarticles) FROM seoul_poem.users, seoul_poem.articles, seoul_poem.pictures where users.idusers = articles.users_idusers and articles.pictures_idpictures = pictures.idpictures and idusers=1';
      let count_photo = await connection.query(query2);

      res.status(201).send({count_photo: count_photo, myphoto : myphoto});
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

// 사용자의 시 조회
router.post('/poem', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      //idusers 수정할것!
      let query1 = 'SELECT idarticles, idpoem, title FROM seoul_poem.users, seoul_poem.articles, seoul_poem.poem where users.idusers = articles.users_idusers and articles.poem_idpoem = poem.idpoem and idusers=1';
      let mypoem = await connection.query(query1);

      let query2 = 'SELECT count(idarticles) FROM seoul_poem.users, seoul_poem.articles, seoul_poem.poem where users.idusers = articles.users_idusers and articles.poem_idpoem = poem.idpoem and idusers=1';
      let count_poem = await connection.query(query2);

      res.status(201).send({count_poem: count_poem, mypoem : mypoem});
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

