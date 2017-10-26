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
        let type = req.headers.type;

        let query1 = 'select * from seoul_poem.users where email = ? and foreign_key_type = ? and author = 1';
        let data = await connection.query(query1, [email,type]);
        if(data.length>0) res.status(403).send({result: 'already'});
        else{

            let query2 = 'update seoul_poem.users set author=1 where email = ? and foreign_key_type = ?';
            await connection.query(query2, [email,type]);

            res.status(201).send({result: "author apply success"});
        }
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


router.get('/', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let email =req.headers.email;
        let type = req.headers.type;

        let query1 = 'SELECT count(*) as count from seoul_poem.users where author = 1';
        var count_authors = await connection.query(query1);

        let query2 = 'SELECT email,foreign_key_type as type, pen_name, profile, inform from seoul_poem.users where author=1 and (not (email = ?) or not (foreign_key_type = ?)) order by rand()';
        var authors_list = await connection.query(query2,[email,type]);

        let query3 = 'SELECT email,foreign_key_type as type, pen_name, profile, inform from seoul_poem.users where author=1 and email = ? and foreign_key_type = ?';
        var myinform = await connection.query(query3,[email,type]);

        console.log(myinform);
        if(myinform.length){
            var author_list = [];
            author_list.push(myinform[0]);
            let len = authors_list.length;
            let i = 0;
            for(i=0;i<len;i++){
                author_list.push(authors_list[i]);
            }
            res.status(200).send({count_authors: count_authors[0].count, authors_list: author_list});
        }else {
            res.status(200).send({count_authors: count_authors[0].count, authors_list: authors_list});
        }

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
