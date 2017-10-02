const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');


// 작품담기에 담기
router.post('/:idarticles', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let query1 = 'select articles_idarticles from seoul_poem.bookmarks where articles_idarticles = ?';
      let data = await connection.query(query1, req.params.idarticles);
      if(data.length>0) res.status(403).send({result: '이미 작품을 담았습니다'});


      let query2 = 'select pictures_idpictures, users_idusers from seoul_poem.articles where idarticles = ?';
      let selected = await connection.query(query2, req.params.idarticles);

      let bookmark= {
        articles_idarticles : req.params.idarticles,
        articles_pictures_idpictures : selected[0].pictures_idpictures,
        articles_users_idusers : selected[0].users_idusers,
        users_idusers: 1 //나중에 수정할것!
      };


      let query3 = 'insert into seoul_poem.bookmarks set ?';
      await connection.query(query3, bookmark);


      res.status(201).send({result: "bookmark success"});
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

// 사용자 아이디를 이용하여 담은 작품 보기
router.get('/search', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      //userid로 변경할 것!!!
      let query1 = 'SELECT articles_idarticles FROM seoul_poem.bookmarks where users_idusers = 1';
      var selected = await connection.query(query1);
      console.log(selected[0].articles_idarticles);
      console.log(selected);


      let query2 = 'SELECT idarticles, profile, poem.title, poem.content, photo FROM seoul_poem.articles, seoul_poem.poem , seoul_poem.users ,seoul_poem.pictures where idarticles = ?  and articles.poem_idpoem = poem.idpoem and articles.users_idusers = users.idusers and articles.pictures_idpictures = pictures.idpictures;';
      let bookmark_list=[];

      for(var i=0; i<selected.length; i++){
        bookmark_list[i] = await connection.query(query2, selected[i].articles_idarticles);
      }

      res.status(200).send({bookmark_list: bookmark_list});
      //res.status(200).send({result: "bookmark search success"});
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
