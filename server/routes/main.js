const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool');
const jwt = require('jsonwebtoken');

    router.get('/', async (req, res) => {
    try{
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        req.checkQuery({tag : {notEmpty: true,errorMessage : 'error message'}});
        let errors = req.validationErrors();
        if (errors) {
            res.status(501);
            res.json({status : "fail",msg:err});
            return;
        }
        else{
        let tag = '%#'+req.query.tag+' %';
        let query1 = 'select seoul_poem.pictures.photo,seoul_poem.articles.idarticles,seoul_poem.articles.title from seoul_poem.pictures, seoul_poem.articles where seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.tags like ? order by rand() limit 5;'
        let main_list   = await connection.query(query1,tag);

            res.status(200);
            res.json({status:"success", data: main_list});
            await connection.commit();
        }
    }catch(err){
        console.log(err);
        res.status(500);
        res.json({status : "fail",msg:err});
        connection.rollback();

    }finally{
        pool.releaseConnection(connection);
    }
});

//태그내에 모든 사진들을 보여줌

router.get('/all', async ( req, res) => {
    try{

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        req.checkQuery({tag : {notEmpty: true,errorMessage : 'error message'}});

        let errors = req.validationErrors();
        if (errors) {
            res.status(501);
            res.json({status : "fail",msg:err});
            return;
        } else {
            let tag = '%#' + req.query.tag+ ' %';
            let query1 = "select seoul_poem.pictures.photo,seoul_poem.articles.idarticles from seoul_poem.pictures, seoul_poem.articles where seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.tags like ? order by seoul_poem.articles.idarticles Desc;"
            let main_list = await connection.query(query1, tag);

            res.status(200);
            res.json({sucess:"success", data: main_list});
            await connection.commit();
        }

    }catch(err){
        console.log(err);
        res.status(500);
        res.json({status : "fail",msg:err});
        connection.rollback();

    }finally{
        pool.releaseConnection(connection);
    }
});



//태그나 작가별 검색에 이용 (3개씩 보여주며)

router.get('/search',async(req, res) => {

    try{

        var connection = await pool.getConnection();
        await connection.beginTransaction();
        req.checkQuery({tag : {notEmpty: true,errorMessage : 'error message'}});

        let errors = req.validationErrors();
        if (errors) {
            res.status(501);
            res.json({status : "fail",msg:err});
            return;
        }
        else
        {
            let tag ='%'+ req.query.tag+'%';

            let query2 = "select seoul_poem.users.pen_name as pen_name,seoul_poem.users.idusers as idusers,seoul_poem.users.profile as profile from seoul_poem.users where seoul_poem.users.pen_name like ? order by seoul_poem.users.idusers Desc limit 3"
            let author_list = await connection.query(query2,tag);

            let counts_author = author_list.length;

            let i;
            let query3 = "select count(*) as count from seoul_poem.articles, seoul_poem.poem where seoul_poem.articles.users_idusers = ? and seoul_poem.articles.poem_idpoem = seoul_poem.poem.idpoem";
            let query4 = "select count(*) as count from seoul_poem.articles, seoul_poem.pictures where seoul_poem.articles.users_idusers = ? and seoul_poem.articles.pictures_idpictures = seoul_poem.pictures.idpictures";

            let arr1 = [];

            for (i=0;i<counts_author;i++){
                let author = {};
                author.name_ = author_list[i].pen_name;
                let search_key = author_list[i].idusers;
                author.profile =author_list[i].profile;
                let article_count = await connection.query(query3,search_key);
                author.ac = article_count[0].count;
                let poem_count =  await connection.query(query4,search_key);
                author.pc = poem_count[0].count;
                arr1[i] = author;
         }

            let query1 = "select seoul_poem.articles.idarticles as idarticles,seoul_poem.articles.title as title,seoul_poem.articles.poem_idpoem as idpoem from seoul_poem.articles, seoul_poem.pictures where seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.tags like ? order by seoul_poem.articles.idarticles Desc limit 3;"

            let article_list = await connection.query(query1,tag);

            let counting = article_list.length;

            let arr2 = [];

            let query5 = "select seoul_poem.poem.content as content from  seoul_poem.poem where seoul_poem.poem.idpoem = ?";
            console.log(counting);

            for(i=0 ; i<counting;i++){
                let articlelist= {};
                articlelist.idarticles = article_list[i].idarticles;
                articlelist.title = article_list[i].title;
                if (article_list[i].idpoem == null){
                    articlelist.contents=""
                }
                else {
                    let poem_ = await connection.query(query5,article_list[i].idpoem);
                    articlelist.contents= poem_[0].content;
                }

                arr2[i] = articlelist;
            }
        console.log(arr2);
        res.status(200);
        res.json( { status : "success",author_list : arr1 , article_list : arr2});
        await connection.commit();
        }

    }catch(err){
        console.log(err);
        res.status(500);
        res.json({status : "fail",msg:err});
        connection.rollback();
    }
    finally {
        pool.releaseConnection(connection);
    }
});

module.exports = router;
