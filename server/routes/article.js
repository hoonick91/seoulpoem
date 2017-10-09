
    const express = require('express');
    const aws = require('aws-sdk');
    const moment = require('moment');
    const jwt = require('jsonwebtoken');
    const multer = require('multer');
    const multerS3 = require('multer-s3');
    const crypto = require("crypto");
    const router = express.Router();
    const async = require('async');
    const date = require('date-utils');
    aws.config.loadFromPath('./config/aws_config.json');
    const pool = require('../config/db_pool');
    const s3 = new aws.S3();

    // var upload = multer({ storage: multer.memoryStorage()})

    const upload = multer({
      storage: multerS3({
        s3: s3,
        bucket: 'csr1994',
        acl: 'public-read',
        key: function (req, file, cb) {
            let name_temp =  Date.now().toString() + req.headers.email + file.originalname;
            const cipher = crypto.createCipher('aes-256-cbc',req.app.get('jwt-secret'));
            let result = cipher.update(name_temp, 'utf8', 'base64'); // 'HbMtmFdroLU0arLpMflQ'
            result += cipher.final('base64');
            let imagename = result+".jpg";
            imagename = imagename.replace(/\//g,'');
            cb(null, imagename);
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

          let email = req.headers.email;

          console.log(req.file.location);

          let query_id = 'select idusers as id from seoul_poem.users where seoul_poem.users.email = ?'
          let ids = await  connection.query(query_id,email);


          let query_picture = 'insert into seoul_poem.pictures (photo) VALUES (?);';
          let picture_output = await connection.query(query_picture, req.file.location);

          var dt = new Date();
          var d = dt.toFormat('YYYY-MM-DD HH24:MI:SS');

          var result ="";
          if(req.body.content) { //시가 있으면
              let setting = {
                  font_type: req.body.font_type,
                  font_size: req.body.font_size,
                  bold: req.body.bold,
                  inclination: req.body.inclination,
                  underline: req.body.underline,
                  color: req.body.color,
                  sort: req.body.sortinfo
              };
              let query = 'insert into seoul_poem.setting set ?;';
              let output_ = await connection.query(query, setting);
              let poem = {
                  content: req.body.content,
                  setting_idsettings : output_.insertId
              };
              let query_poem = 'insert into seoul_poem.poem set ?;';
              let poem_output = await connection.query(query_poem, poem);

              let article ={
                  tags: req.body.tags,
                  background: req.body.background,
                  inform: req.body.inform,
                  date: d,
                  title : req.body.title,
                  pictures_idpictures: picture_output.insertId,
                  poem_idpoem: poem_output.insertId,
                  users_idusers: ids[0].id
              }
              let article_q = 'insert into seoul_poem.articles set ?;'
              result =  await connection.query(article_q, article);
          }
          else{

              let article ={
                  tags: req.body.tags,
                  background: req.body.background,
                  inform: req.body.inform,
                  date: d,
                  title : req.body.title,
                  users_idusers: ids[0].id,
                  pictures_idpictures: picture_output.insertId
              }
              let article_q = 'insert into seoul_poem.articles set ?;'
              result =  await connection.query(article_q, article);
          }

          res.status(201).send({status: "success"});
          await connection.commit();
          //}

        }
        catch(err) {
            console.log(err);
            res.status(500).send({status: "fail", msg: err });
        }
        finally {
            pool.releaseConnection(connection);
        }
    });


/*
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
*/


    // 글 수정
    router.put('/:idarticles',upload.single('photo'), async (req, res, next) => {
      try {
            var connection = await pool.getConnection();

            let picture = {
              title: req.body.picture_title
              ////사진은 어떻게 저장??
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

            let query1 = 'select pictures_idpictures, poem_idpoem, setting_idsettings from seoul_poem.articles where idarticles = ?';
            let selected = await connection.query(query1, req.params.idarticles);


            let query2 = 'update seoul_poem.pictures set ? where idpictures = ?';
            await connection.query(query2, [picture, selected[0].pictures_idpictures]);

            let query3 = 'update seoul_poem.poem set ? where idpoem = ?';
            await connection.query(query3, [poem, selected[0].poem_idpoem]);

            let query4 = 'update seoul_poem.setting set ? where idsettings = ?';
            await connection.query(query4, [setting, selected[0].setting_idsettings]);


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

            let query1 = 'SELECT tags, background,inform,date,title,users_idusers as iduser,pictures_idpictures as idpictures , poem_idpoem as idpoem FROM seoul_poem.articles where seoul_poem.articles.idarticles=?';
            let queryresult = await connection.query(query1, req.params.idarticles);
            console.log(queryresult);

            let article={};
            if(queryresult[0].idpoem){
                // 시가 있으면
                let query2 = 'SELECT content, setting_idsettings from poem where idpoem = ?'
                let query2_result = await connection.query(query2,queryresult[0].idpoem);
                console.log(query2_result);
                let query3 = 'SELECT font_type,font_size,bold,inclination,underline,color,sort from setting where idsettings = ?'
                let query3_result = await connection.query(query3,query2_result[0].setting_idsettings);
                console.log(query3_result);

                let setting={};
                setting.font_type = query3_result[0].font_type;
                setting.font_size  = query3_result[0].font_size;
                setting.bold  = query3_result[0].bold;
                setting.inclination = query3_result[0].inclination;
                setting.underline = query3_result[0].underline;
                setting.color = query3_result[0].color;
                setting.sort = query3_result[0].sort;

                article.setting = setting;
                article.content = query2_result[0].content;
            }
            else{
                article.content = "";
            }
            article.tags = queryresult[0].tags;
            article.background = queryresult[0].background;
            article.inform = queryresult[0].inform;
            article.date = queryresult[0].date;
            article.title = queryresult[0].title;

            let query4 = 'select photo from pictures where idpictures = ?'
            let query4_result = await connection.query(query4,queryresult[0].idpictures);
            console.log(query4_result);
            article.photo = query4_result[0].photo;

            let query5 = 'select profile,pen_name from users where idusers = ?'
            let query5_result = await connection.query(query5,queryresult[0].iduser);
            console.log(query5_result);

            let user ={}
            user.profile = query5_result[0].profile;
            user.pen_name = query5_result[0].pen_name;

            let query6 = 'select pictures.photo as photo ,articles.idarticles as idarticles from articles,pictures where articles.users_idusers = ? and articles.idarticles != ? and articles.pictures_idpictures = pictures.idpictures order by articles.idarticles DESC limit 5;'
            let query6_result = await connection.query(query6,[queryresult[0].iduser,req.params.idarticles]);
            console.log(query6_result);
            var i;
            let others =[];
            var index=0;
            for(i=0 ; i<query6_result.length;i++ ){
                let photo = {};
                if(query6_result[i].idarticles != req.params.idarticles)
                {
                    photo.idarticles = query6_result[i].idarticles;
                    photo.photo = query6_result[i].photo;
                    others[index] = photo;
                    index++;
                }
            }
            user.others = others;
            article.user = user;

            res.status(200).json( {status : "success",article: article});
            await connection.commit();
            //}

        }
        catch(err){
            console.log(err);
            res.status(500).json( {status : "fail", msg : err });
            connection.rollback();
        }
        finally{
            pool.releaseConnection(connection);
        }

    });



/*
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
*/



    //글 하나 간단 조회 /article/simple/{articleid}
    router.get('/simple/:idarticles', async (req, res) => {
        try {

            var connection = await pool.getConnection();
            await connection.beginTransaction();


            console.log(req.params.idarticles);
            let query1 = 'SELECT seoul_poem.pictures.photo as photo, seoul_poem.articles.users_idusers as iduers,seoul_poem.articles.tags as tags FROM seoul_poem.articles, seoul_poem.pictures where seoul_poem.articles.idarticles=? and seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures';
            console.log(query1);
            let article = await connection.query(query1, req.params.idarticles);

            console.log(article[0]);
            var userid = article[0].iduers;
            let query2 = 'select seoul_poem.users.profile as profile,seoul_poem.users.pen_name as userName from seoul_poem.users where seoul_poem.users.idusers = ?'
            let author = await connection.query(query2,userid);

            let detail_={};
            detail_.photo = article[0].photo;
            detail_.tags=article[0].tags;
            detail_.profile =author[0].profile;
            detail_.userName = author[0].userName;

            res.status(200);
            res.json({status:"success", data: detail_});
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
