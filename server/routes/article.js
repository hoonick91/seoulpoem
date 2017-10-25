
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
            console.log("s3 check");
            console.log(file);
            let name_temp =  Date.now().toString() +  req.headers.email+ req.headers.type+ file.originalname;
            const cipher = crypto.createCipher('aes-256-cbc',req.app.get('jwt-secret'));
            let result = cipher.update(name_temp, 'utf8', 'base64'); // 'HbMtmFdroLU0arLpMflQ'
            result += cipher.final('base64');
            let imagename = result+".jpg";
            imagename = imagename.replace(/\//g,'');
            cb(null, imagename);
        }
    })
});


var s3key_= "insert into s3key set ?";
var location_key_q_= "select key_ from s3key where location = ?";

var multiupload = upload.fields([{name:'photo',maxCount:1}]);


router.post('/',multiupload, async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();


        console.log(req.files['photo'][0].location);
        let email = req.headers.email;
        let type = req.headers.type;


        let query_picture = 'insert into seoul_poem.pictures (photo) VALUES (?);';
        let picture_output = await connection.query(query_picture, req.files['photo'][0].location);
        await connection.query(s3key_,{location : req.files['photo'][0].location, key_ : req.files['photo'][0].key});

        var dt = new Date();
        var d = dt.toFormat('YYYY-MM-DD HH24:MI:SS');

        var result ="";
        if(req.body.content) {
            let setting = {
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
                users_email: email,
                users_foreign_key_type: type
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
                users_email: email,
                users_foreign_key_type: type,
                pictures_idpictures: picture_output.insertId
            }
            let article_q = 'insert into seoul_poem.articles set ?;'
            result =  await connection.query(article_q, article);
        }

        res.status(201).send({status: "success", articles_id :result.insertId});
        await connection.commit();
        //}

    }
    catch(err) {
        console.log("")
        console.log(err);
        res.status(500).send({status : "fail", msg: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

router.get('/:idarticles', async (req, res) => {
    try {

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let type_ = req.headers.type;
        let email_ = req.headers.email;

        let query1 = 'SELECT tags, background,inform,date,title,users_email as email,users_foreign_key_type as type,pictures_idpictures as idpictures , poem_idpoem as idpoem FROM seoul_poem.articles where seoul_poem.articles.idarticles=?';
        let queryresult = await connection.query(query1, req.params.idarticles);
        console.log(queryresult);

        let article={};
        if(queryresult[0].idpoem){
            let query2 = 'SELECT content, setting_idsettings from poem where idpoem = ?'
            let query2_result = await connection.query(query2,queryresult[0].idpoem);
            console.log(query2_result);
            let query3 = 'SELECT font_size,bold,inclination,underline,color,sort from setting where idsettings = ?'
            let query3_result = await connection.query(query3,query2_result[0].setting_idsettings);
            console.log(query3_result);

            let setting={};
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
            let setting={};
            setting.font_size  = 0;
            setting.bold  = 0;
            setting.inclination = 0;
            setting.underline = 0;
            setting.color = 0;
            setting.sort = 0;
            article.setting = setting;
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

        let query5 = 'select profile,pen_name from users where email = ? and foreign_key_type = ?'
        let query5_result = await connection.query(query5,[queryresult[0].email,queryresult[0].type]);
        console.log(query5_result);

        let user ={}
        user.profile = query5_result[0].profile;
        user.pen_name = query5_result[0].pen_name;
        user.email = queryresult[0].email;
        user.type = queryresult[0].type;

        let query6 = 'select pictures.photo as photo ,articles.idarticles as idarticles from articles,pictures where articles.users_email = ? and articles.users_foreign_key_type = ? and articles.idarticles != ? and articles.pictures_idpictures = pictures.idpictures order by articles.idarticles DESC limit 5;'
        let query6_result = await connection.query(query6,[queryresult[0].email,queryresult[0].type,req.params.idarticles]);
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


        article.writer = user;


        if(type_ == queryresult[0].type && email_ == queryresult[0].email)
            article.modifiable = 1;
        else  article.modifiable = 0;

        res.status(200).json( {status : "success",article: article});
        await connection.commit();
        //}

    }
    catch(err){
        console.log(err);
        res.status(500).send({status : "fail", msg: err });
        connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }

});



router.get('/simple/:idarticles', async (req, res) => {
    try {

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let type_ = req.headers.type;
        let email_ = req.headers.email;

        console.log(req.params.idarticles);
        let query1 = 'SELECT seoul_poem.pictures.photo as photo, seoul_poem.articles.users_email as email,seoul_poem.articles.inform as inform,seoul_poem.articles.users_foreign_key_type as type,seoul_poem.articles.tags as tags FROM seoul_poem.articles, seoul_poem.pictures where seoul_poem.articles.idarticles=? and seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures';
        console.log(query1);
        let article = await connection.query(query1, req.params.idarticles);

        console.log(article[0]);
        var email = article[0].email;
        var type = article[0].type;

        let query2 = 'select seoul_poem.users.profile as profile,seoul_poem.users.pen_name as pen_name from seoul_poem.users where seoul_poem.users.email = ? and seoul_poem.users.foreign_key_type = ?'
        let author = await connection.query(query2,[email,type]);

        let detail_={};
        detail_.photo = article[0].photo;
        detail_.tags=article[0].tags;
        detail_.inform=article[0].inform;
        let writer = {};

        writer.profile =author[0].profile;
        writer.pen_name = author[0].pen_name;
        writer.email = email;
        writer.type = type;

        if(type_ ==type && email_ == email)
            detail_.modifiable = 1;
        else  detail_.modifiable = 0;
        detail_.writer = writer;

        res.status(200);
        res.json({status:"success", data: detail_});
        await connection.commit();

    }
    catch(err){
        console.log(err);
        res.status(500).send({status : "fail", msg: err });
        connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }

});


router.put('/:idarticles', upload.single('photo'), async (req, res, next) => {
    try {
        var connection = await pool.getConnection();

        let email = req.headers.email;
        let type = req.headers.type;

        let query = 'select * from seoul_poem.users where users.email = ? and users.foreign_key_type = ?';
        let login = await connection.query(query, [email,type]);

        if(login.length==0) res.status(403).send({result: 'not member'});
        else{

            let query1 = 'select tags,title,background,inform,date,if(poem_idpoem is null, -1,poem_idpoem) as poem_idpoem from seoul_poem.articles where idarticles = ?';
            let article_data = await connection.query(query1, req.params.idarticles);
            if(article_data.length==0) {
                res.status(403).send({result: 'not exist idarticles'});
            }
            else{
                var dt = new Date();
                var d = dt.toFormat('YYYY-MM-DD HH24:MI:SS');

                if(article_data[0].poem_idpoem == -1){
                    if(req.body.content!=""){
                        let setting = {
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
                            poem_idpoem: poem_output.insertId
                        };
                        let query5 = 'update seoul_poem.articles set ? where idarticles = ?';
                        await connection.query(query5, [article, req.params.idarticles]);

                        res.status(201).send({result: "update article success"});
                        await connection.commit();

                    }else{
                        let article = {
                            title: req.body.title? req.body.title: article_data[0].title,
                            tags: req.body.tags? req.body.tags: article_data[0].tags,
                            background: req.body.background? req.body.background: article_data[0].background,
                            inform: req.body.inform? req.body.inform: article_data[0].inform,
                            date: d
                        };
                        let query5 = 'update seoul_poem.articles set ? where idarticles = ?';
                        await connection.query(query5, [article, req.params.idarticles]);

                        res.status(201).send({result: "update article success"});
                        await connection.commit();
                    }
                }
                else{
                    let query1_1 = 'select * from seoul_poem.poem where idpoem = ?';
                    let poem_data = await connection.query(query1_1, article_data[0].poem_idpoem);

                    let query1_2 = 'SELECT * FROM seoul_poem.setting where idsettings = ?';
                    let setting_data = await connection.query(query1_2, poem_data[0].setting_idsettings);


                    let poem = {
                        content: req.body.content?  req.body.content: poem_data[0].content
                    };

                    let setting = {
                        font_size: req.body.font_size? req.body.font_size: setting_data[0].font_size,
                        bold: req.body.bold? req.body.bold: setting_data[0].bold,
                        inclination: req.body.inclination? req.body.inclination: setting_data[0].inclination,
                        underline: req.body.underline? req.body.underline: setting_data[0].underline,
                        color: req.body.color? req.body.color: setting_data[0].color,
                        sort: req.body.sort?  req.body.sort: setting_data[0].sort
                    };


                    let query3 = 'update seoul_poem.setting set ? where idsettings = ?';
                    await connection.query(query3, [setting, poem_data[0].setting_idsettings]);


                    let query4 = 'update seoul_poem.poem set ? where idpoem = ?';
                    await connection.query(query4, [poem, article_data[0].poem_idpoem]);

                    let article = {
                        title: req.body.title? req.body.title: article_data[0].title,
                        tags: req.body.tags? req.body.tags: article_data[0].tags,
                        background: req.body.background? req.body.background: article_data[0].background,
                        inform: req.body.inform? req.body.inform: article_data[0].inform,
                        date: d
                    };

                    let query5 = 'update seoul_poem.articles set ? where idarticles = ?';
                    await connection.query(query5, [article, req.params.idarticles]);

                    res.status(201).send({result: "update article success"});
                    await connection.commit();
                }
            }
        }

    }
    catch(err) {
        console.log(err);
        res.status(500).send({status : "fail", msg: err });
        connection.rollback();
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
        if(data1.length==0) res.status(403).send({result: '占쏙옙占쏙옙占쏙옙占쏙옙 占십댐옙 占쏙옙 id占쌉니댐옙.'});


        let query1 = 'SELECT * FROM seoul_poem.bookmarks where articles_idarticles=?';
        let data2 = await connection.query(query1, req.params.idarticles) || null;
        if(data2.length>0){
            let query2 = 'delete from seoul_poem.bookmarks where articles_idarticles = ?';
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
        res.status(500).send({status : "fail", msg: err });
        connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }

});

module.exports = router;
