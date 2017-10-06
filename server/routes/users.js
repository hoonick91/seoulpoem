const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const bcrypt = require('bcryptjs'); //해쉬용 확장모듈, 윈도우에서 돌릴때는 js 빼고 돌려야 함
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



/* GET users listing. */
router.post('/signin', function(req, res) {
    req.checkBody('pen_name','필명을 입력해주세요.').notEmpty();
    req.checkBody('token','토큰이 입력되지 않았습니다.').notEmpty();
    req.checkBody('type','타입을 입력해주세요.').notEmpty();
    let errors = req.validationErrors();
    if(!errors){
        //연결
        
    }
    else {
        // error
    }


    var pen_name = req.body.penname;
    var token_ = req.body.token;
    var type_ = req.body.type;


});


module.exports = router;
