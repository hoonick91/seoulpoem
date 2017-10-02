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
      // let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
      // let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
      //
      // if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
      // else {
      // var decoded_pk = jwt.decode(token, {complete: true});

      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let picture = {
        photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
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


      let query1 = 'insert into seoul_poem.pictures set ?';
      await connection.query(query1, picture);

      let query2 = 'insert into seoul_poem.setting set ?';
      await connection.query(query2, setting);

      let query2_1 = 'SELECT idsettings FROM seoul_poem.setting order by idsettings desc limit 1';
      let selected = await connection.query(query2_1);


      let poem = {
        title: req.body.poem_title,
        content: req.body.content,
        setting_idsettings : selected[0].idsettings
      };

      let query3 = 'insert into seoul_poem.poem set ?';
      await connection.query(query3, poem);



      let query4 = 'SELECT idpictures FROM seoul_poem.pictures order by idpictures desc limit 1';
      let selected4 = await connection.query(query4);

      let query5 = 'SELECT idpoem FROM seoul_poem.poem order by idpoem desc limit 1';
      let selected5 = await connection.query(query5);


      let article = {
        tags: req.body.tags,
        background: req.body.background,
        inform: req.body.inform,
        date: req.body.date,
        pictures_idpictures: selected4[0].idpictures,
        poem_idpoem: selected5[0].idpoem,
        users_idusers: 1 //나중에 수정할 것!
      };


      let query6 = 'insert into seoul_poem.articles set ?';
      await connection.query(query6, article);

      res.status(201).send({result: "success"});
      await connection.commit();
      //}
    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// 글 수정
router.put('/:idarticles',upload.single('photo'), async (req, res, next) => {
  try {
        var connection = await pool.getConnection();

        let picture = {
          photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
        };

        let poem = {
          title: req.body.poem_title,
          content: req.body.content
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

        let query1 = 'select pictures_idpictures, poem_idpoem from seoul_poem.articles where idarticles = ?';
        let selected = await connection.query(query1, req.params.idarticles);


        let query1_1 = 'select setting_idsettings from seoul_poem.poem where idpoem = ?';
        let selected2 = await connection.query(query1_1, selected[0].poem_idpoem);


        let query2 = 'update seoul_poem.pictures set ? where idpictures = ?';
        await connection.query(query2, [picture, selected[0].pictures_idpictures]);

        let query3 = 'update seoul_poem.poem set ? where idpoem = ?';
        await connection.query(query3, [poem, selected[0].poem_idpoem]);


        let query4 = 'update seoul_poem.setting set ? where idsettings = ?';
        await connection.query(query4, [setting, selected2[0].setting_idsettings]);


        res.status(201).send({result: "update article success"});

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});


//글 하나 조회 /article/{articleid}
router.get('/:idarticles', async (req, res) => {
    try {
        //
        // let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        // let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
        //
        // if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
        // else {
        // var decoded_pk = jwt.decode(token, {complete: true});

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT * FROM seoul_poem.articles, seoul_poem.poem, seoul_poem.pictures, seoul_poem.setting where seoul_poem.articles.idarticles=? and seoul_poem.articles.poem_idpoem=seoul_poem.poem.idpoem and seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.poem.setting_idsettings=seoul_poem.setting.idsettings';
        let article_list = await connection.query(query1, req.params.idarticles);

        res.status(200).send( { article_list: article_list });
        await connection.commit();
        //}

    }
    catch(err){
        console.log(err);
        res.status(500).send( { result: err });
        connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }

});

router.delete('/:idarticles', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let query = 'SELECT * FROM seoul_poem.articles where idarticles = ? ';
      let data1 = await connection.query(query, req.params.idarticles) || null;
      if(data1.length==0) res.status(403).send({result: '존재하지 않는 글 id입니다.'});

      //users_idusers 수정할것!
      let query1 = 'SELECT * FROM seoul_poem.bookmarks where articles_idarticles=? and users_idusers=1';
      let data2 = await connection.query(query1, req.params.idarticles) || null;
      if(data2.length>0){
        let query2 = 'delete from seoul_poem.bookmarks where articles_idarticles = ? and users_idusers=1';
        await connection.query(query2, req.params.idarticles);
      }

      let query3 = 'SELECT idpictures FROM seoul_poem.articles, pictures where articles.pictures_idpictures = pictures.idpictures and idarticles = ?';
      let selected = await connection.query(query3, req.params.idarticles);

      let query4 = 'SELECT idpoem, idsettings FROM seoul_poem.articles, poem, setting where articles.poem_idpoem = poem.idpoem and poem.setting_idsettings = setting.idsettings and idarticles = ?';
      let selected2 = await connection.query(query4, req.params.idarticles);

      let query5 = 'delete from seoul_poem.articles where idarticles = ?';
      await connection.query(query5, req.params.idarticles);

      let query6 = 'delete from seoul_poem.pictures where idpictures = ?';
      await connection.query(query6, selected[0].idpictures);

      let query7 = 'delete from seoul_poem.poem where idpoem = ?';
      await connection.query(query7, selected2[0].idpoem);

      let query8 = 'delete from seoul_poem.setting where idsettings = ?';
      await connection.query(query8, selected2[0].idsettings);




      res.status(200).send({result: 'delete success'});
      await connection.commit();
  }

  catch(err){
      console.log(err);
      res.status(500).send( { result: err });
      connection.rollback();
  }
  finally{
      pool.releaseConnection(connection);
  }

});

module.exports = router;
