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

router.post('/:idarticles', async (req, res) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let email =req.headers.email;

      let query1 = 'select articles.pictures_idpictures as idpic, articles.idarticles as ida, articles.users_idusers as iduser from seoul_poem.articles where idarticles = ?';
      let selected = await connection.query(query1, req.params.idarticles);

      let query2 = 'select users.idusers as users from seoul_poem.users where email = ?';

      let userinfo = await connection.query(query2,email);

      let bookmark= {
          users_idusers: userinfo[0].users,
          articles_idarticles : selected[0].ida,
          articles_pictures_idpictures : selected[0].idpic,
          articles_users_idusers : selected[0].iduser
      };

      let query4 = 'select * from bookmarks where users_idusers = ? and articles_idarticles = ?'
      let check_db = await connection.query(query4, [userinfo[0].users,selected[0].ida]);
      console.log (check_db);
      if(check_db.length != 0 ){
          let query3 = 'delete from seoul_poem.bookmarks where users_idusers = ? and articles_idarticles = ?';
          await connection.query(query3, [userinfo[0].users,selected[0].ida]);
          res.status(201).json({status : "success", mag: "bookmark delete"});
      }
      else{
          let query3 = 'insert into seoul_poem.bookmarks set ?';
          await connection.query(query3, bookmark);
          res.status(201).json({status : "success", mag: "bookmark insert"});
      }
/*
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
*/


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
router.get('/search', async (req, res) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let email = req.headers.email;
      //userid로 변경할 것!!

      let query1 = "SELECT seoul_poem.bookmarks.articles_idarticles as idarticles FROM seoul_poem.bookmarks, seoul_poem.users where seoul_poem.users.email = ? and seoul_poem.bookmarks.users_idusers = seoul_poem.users.idusers";
      var selected = await connection.query(query1,email);

      let query2 = 'SELECT articles.idarticles as idarticles, pictures.photo as photo, users.profile as profile,users.pen_name as pen_name, articles.title as title,articles.poem_idpoem as idpoem FROM seoul_poem.articles, seoul_poem.users ,seoul_poem.pictures where idarticles = ? and articles.users_idusers = users.idusers and articles.pictures_idpictures = pictures.idpictures;';
      let bookmark_list=[]
      let query3 = "SELECT poem.content as content from poem where idpoem = ?"
      let len_ = selected.length;
      for(var i=0; i<len_; i++){
          let bookmark={};
          let tmepbookmark = await connection.query(query2, selected[i].idarticles);
          bookmark.idarticles = tmepbookmark[0].idarticles;
          bookmark.photo = tmepbookmark[0].photo;
          bookmark.pen_name = tmepbookmark[0].pen_name;
          bookmark.profile =tmepbookmark[0].profile;
          bookmark.title = tmepbookmark[0].title;
          if(tmepbookmark[0].poem_idpoem==null){
              bookmark.content = "";
          }else {
            let temppoem = await connection.query(query3,tmepbookmark[0].poem_idpoem);
            bookmark.content = temppoem[0].content;
          }

          bookmark_list[i]=bookmark;
/*
      //userid로 변경할 것!!!
      let query1 = 'SELECT articles_idarticles FROM seoul_poem.bookmarks where users_idusers = 1';
      var selected = await connection.query(query1);
      console.log(selected[0].articles_idarticles);
      console.log(selected);


      let query2 = 'SELECT idarticles, profile, poem.title, poem.content, photo FROM seoul_poem.articles, seoul_poem.poem , seoul_poem.users ,seoul_poem.pictures where idarticles = ?  and articles.poem_idpoem = poem.idpoem and articles.users_idusers = users.idusers and articles.pictures_idpictures = pictures.idpictures;';
      let bookmark_list=[];

      for(var i=0; i<selected.length; i++){
        bookmark_list[i] = await connection.query(query2, selected[i].articles_idarticles);
*/
      }

      res.status(200).json({status : "success", bookmark_list: bookmark_list});
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
