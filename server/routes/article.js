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

// /article 글 저장 등록
router.post('/',upload.single('photo'), async (req, res, next) => {
  try {
      let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
      let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

      if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
      else {
      var decoded_pk = jwt.decode(token, {complete: true});

      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let picture = {
        picture_title: req.body.title
        ////사진은 어떻게 저장??
      };

      let poem = {
        poem_title: req.body.title,
        poem_content: req.body.content
      };

      let setting = {
        font_type: req.body.font_type,
        font_size: req.body.font_size,
        bold: req.body.bold,
        inclination: req.body.inclination,
        underline: req.body.underline,
        color: req.body.color,
        sort: req.body.sort
      };

      let article = {
        tags:req.body.tags,
        background:req.body.background,
        inform:req.body.inform,
        date:req.body.date
      };


      let query1 = 'insert into seoul_poem.pictures set ?';
      await connection.query(query1, picture);

      let query1_1 = 'select id from seoul_poem.pictures where title =?';
      let selected = await connection.query(query1_1, req.body.title);
      console.log(selected[0].id);

      let query2 = 'insert into seoul_poem.poem set ?';
      await connection.query(query2, poem);

      let query3 = 'insert into seoul_poem.setting set ?';
      await connection.query(query3, setting);

      let query4 = 'insert into seoul_poem.articles set ?';
      await connection.query(query3, article);

      res.status(201).send({result: "success"});
      await connection.commit();
      }
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
