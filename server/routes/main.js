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
            console.log(errors);
            res.status(501);
            res.json({status : "fail",msg:errors});
            return;
        }
        else
        {
            let tag ='%'+ req.query.tag+'%';

            let query2 = "select seoul_poem.users.pen_name as pen_name,seoul_poem.users.email as email,seoul_poem.users.profile as profile,seoul_poem.users.foreign_key_type as type from seoul_poem.users where seoul_poem.users.pen_name like ? limit 5"
            let author_list = await connection.query(query2,tag);

            let counts_author = author_list.length;

            let i;
            let query3 = "select count(*) as count from seoul_poem.articles, seoul_poem.poem where seoul_poem.articles.users_email = ? and seoul_poem.articles.users_foreign_key_type = ? and seoul_poem.articles.poem_idpoem = seoul_poem.poem.idpoem";
            let query4 = "select count(*) as count from seoul_poem.articles, seoul_poem.pictures where seoul_poem.articles.users_email = ? and seoul_poem.articles.users_foreign_key_type = ? and seoul_poem.articles.pictures_idpictures = seoul_poem.pictures.idpictures";

            let arr1 = [];

            for (i=0;i<counts_author;i++){
                let author = {};
                author.name_ = author_list[i].pen_name;
                let email = author_list[i].email;
                let type = author_list[i].type;
                author.email = email;
                author.type = type;
                author.profile =author_list[i].profile;
                let article_count = await connection.query(query3,[email,type]);
                author.ac = article_count[0].count;
                let poem_count =  await connection.query(query4,[email,type]);
                author.pc = poem_count[0].count;
                arr1[i] = author;
         }

            let query1 = "select seoul_poem.articles.idarticles as idarticles,seoul_poem.articles.title as title,if(seoul_poem.articles.poem_idpoem IS NULL, -1,seoul_poem.articles.poem_idpoem) as idpoem from seoul_poem.articles, seoul_poem.pictures where seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.title like ? order by seoul_poem.articles.idarticles Desc limit 5;"
            let article_list = await connection.query(query1,tag);

            let counting = article_list.length;
            let query6 = "select seoul_poem.articles.idarticles as idarticles,seoul_poem.articles.title as title,if(seoul_poem.articles.poem_idpoem IS NULL, -1,seoul_poem.articles.poem_idpoem) as idpoem from seoul_poem.articles, seoul_poem.pictures where seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.tags like ? order by rand() limit ?;"

            var article_list2;
            if(counting <5){
                article_list2 = await connection.query(query6,[tag,5-counting]);
            }

            let arr_check  = [];
            let arr2 = [];

            let query5 = "select seoul_poem.poem.content as content from  seoul_poem.poem where seoul_poem.poem.idpoem = ?";
            console.log(counting);

            for(i=0 ; i<counting;i++){
                let articlelist= {};
                arr_check.push(article_list[i].idarticles);
                articlelist.idarticles = article_list[i].idarticles;
                articlelist.title = article_list[i].title;
                if (article_list[i].idpoem == -1){
                    articlelist.contents=""
                }
                else {
                    let poem_ = await connection.query(query5,article_list[i].idpoem);
                    articlelist.contents= poem_[0].content;
                }

                arr2[i] = articlelist;
            }

            if(counting <5){
                let counting2 =article_list2.length;
                for(i=0; i<counting2;i++){
                    let j;
                    let flag_=0;
                    for(j=0;j<arr_check.length;j++){
                        if(arr_check[j]==article_list2[i].idarticles) flag_=1;
                    }
                    if(flag_==0){
                        let articlelist= {};
                        articlelist.idarticles = article_list2[i].idarticles;
                        articlelist.title = article_list2[i].title;
                        if (article_list2[i].idpoem == -1){
                            articlelist.contents=""
                        }
                        else {
                            let poem_ = await connection.query(query5,article_list2[i].idpoem);
                            articlelist.contents= poem_[0].content;
                        }

                        arr2[i+counting] = articlelist;
                    }
                }
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
