
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

// 프로필 정보 조회
router.get('/', async (req, res)=> {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let email = req.headers.email;
        let type = req.headers.type;


        //idusers 수정할것!
        let query1 = 'select profile,background,inform,pen_name from seoul_poem.users where users.email = ? and users.foreign_key_type = ?';
        let mypage = await connection.query(query1, [email,type]);

        let result =mypage[0];


        res.status(201).json({status :"success" ,msg : result});
        await connection.commit();

    }
    catch(err) {
        console.log(err);
        res.status(500).send({status : "fail", msg: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// 사용자의 그림을 조회
router.get('/photo', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let email = req.headers.email;
        let type = req.headers.type;

        //idusers 수정할것!
        let query1 = 'SELECT articles.idarticles as idarticles, pictures.photo as photo, if(articles.poem_idpoem IS NULL,-1,articles.poem_idpoem )as idpoem FROM seoul_poem.articles, seoul_poem.pictures where articles.users_email = ? and articles.users_foreign_key_type = ? and articles.pictures_idpictures = pictures.idpictures  order by seoul_poem.articles.idarticles DESC';
        let myphoto = await connection.query(query1,[email,type]);

        let result ={};
        result.counts = myphoto.length;


        result.photos = myphoto;

        res.status(201).json({status :"success", msg : result});
        await connection.commit();

    }
    catch(err) {
        console.log(err);
        res.status(500).send({status : "fail", msg: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// 사용자의 시 조회
router.get('/poem', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let email = req.headers.email;
        let type = req.headers.type;

        let query1 = 'SELECT articles.idarticles, articles.title FROM seoul_poem.articles where users_email = ? and users_foreign_key_type = ? and poem_idpoem IS NOT NULL order by seoul_poem.articles.idarticles DESC';
        let mypoem = await connection.query(query1,[email,type]);

        let result ={};
        result.counts = mypoem.length;
        result.poems = mypoem;

        res.status(201).json({status : "success", msg :result });
        await connection.commit();

    }
    catch(err) {
        console.log(err);
        res.status(500).send({status : "fail", msg: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});



module.exports = router;

