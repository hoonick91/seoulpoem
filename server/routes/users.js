const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const bcrypt = require('bcryptjs'); //해쉬용 확장모듈, 윈도우에서 돌릴때는 js 빼고 돌려야 함
const crypto = require("crypto");
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
    req.checkBody('email','토큰이 입력되지 않았습니다.').notEmpty();
    req.checkBody('type','타입을 입력해주세요.').notEmpty();
    let errors = req.validationErrors();
    if(!errors){

    }
    else {
        // error
    }
    const cipher = crypto.createCipher('aes-256-cbc',req.app.get('jwt-secret') );
    let result = cipher.update(req.body.email, 'utf8', 'base64'); // 'HbMtmFdroLU0arLpMflQ'
    result += cipher.final('base64');

    const decipher = crypto.createDecipher('aes-256-cbc', req.app.get('jwt-secret'));
    let result2 = decipher.update(result, 'base64', 'utf8'); // 암호화할문 (base64, utf8이 위의 cipher과 반대 순서입니다.)
    result2 += decipher.final('utf8'); // 암호화할문장 (여기도 base64대신 utf8)

    console.log(result);


    let token = jwt.sign({data : req.body.email},req.app.get('jwt-secret'),{algorithm : 'HS256', expiresIn : 1440});


    res.status(200);
    res.json({result : result, result2 : result2, token : token});

});


module.exports = router;
