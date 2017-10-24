const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');


router.post('/', async (req, res, next) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let email =req.headers.email;

        let query1 = 'select email from seoul_poem.users where email = ? and author = 1';
        let data = await connection.query(query1, email);
        if(data.length>0) res.status(403).send({result: 'already'});


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


router.get('/', async (req, res, next) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT count(*) as count from seoul_poem.users where author = 1';
        var count_authors = await connection.query(query1);

        let query2 = 'SELECT email,foreign_key_type as type, pen_name, profile, inform from seoul_poem.users where author=1';
        var authors_list = await connection.query(query2);

        res.status(200).send({count_authors: count_authors[0].count, authors_list: authors_list});
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
