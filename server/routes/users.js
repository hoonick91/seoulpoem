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
            let name_temp =  Date.now().toString() + req.headers.email+ req.headers.type + file.originalname+req.fieldname;
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


/*
*    const cipher = crypto.createCipher('aes-256-cbc',req.app.get('jwt-secret') );
    let result = cipher.update(req.body.email, 'utf8', 'base64'); // 'HbMtmFdroLU0arLpMflQ'
    result += cipher.final('base64');

    const decipher = crypto.createDecipher('aes-256-cbc', req.app.get('jwt-secret'));
    let result2 = decipher.update(result, 'base64', 'utf8'); // 암호화할문 (base64, utf8이 위의 cipher과 반대 순서입니다.)
    result2 += decipher.final('utf8'); // 암호화할문장 (여기도 base64대신 utf8)
*
*
    let token = jwt.sign({data : req.body.email},req.app.get('jwt-secret'),{algorithm : 'HS256', expiresIn : 1440});
* */

/* GET users listing. */

//로그인 자체는 Clinet 에서 SNS 연동을 하기 때문에 DB에 없으면 넣구 있으면 로그인이 진행되는 방식으로 처리

router.post('/login', async (req, res) => {
try {

    req.checkHeaders('email', '이메일 입력되지 않았습니다.').notEmpty();
    req.checkHeaders('type', '타입을 입력해주세요.').notEmpty();

    let errors = req.validationErrors();
    if (!errors) {

        let email = req.headers.email;
        let type = req.headers.type;

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let check_emailandtype_query = 'Select * from users where email =? and foreign_key_type = ?';
        var check_emailandtype = await connection.query(check_emailandtype_query,[email,type]);

        if(check_emailandtype != 0){
            res.status(200);
            res.json({status: "success", msg: "로그인을 성공하였습니다."});
        }
        else {
            res.status(200);
            res.json({status: "fail", msg: "회원 가입이 필요합니다."});
        }

    }
    else {
        // error
        res.status(401);
        res.json({status: "fail", msg: errors});
    }
}
catch(err) {
    console.log(err);
    res.status(500).json({status:"fail",msg: err });
}
finally {
    pool.releaseConnection(connection);
}
});


router.post('/signin', async (req, res) => {
try{

    req.checkBody("pen_name","필명을 입력해주세요.").notEmpty();

    let errors = req.validationErrors();
    if (!errors) {
        let pen_name = req.body.pen_name;
        let email = req.headers.email;
        let type = req.headers.type;

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let check_penname_query = 'Select * from users where pen_name = ?';
        var check_penname = await connection.query(check_penname_query, pen_name);

        if (check_penname.length != 0) {
            res.status(401);
            res.json({status: "fail", msg: "사용 중인 필명입니다. 다른 필명을 입력해주세요."});
        }
        else {
            let user = {
                foreign_key_type: type,
                email: email,
                pen_name: pen_name,
                inform: "",

            }
            let sigin_query = 'insert into users set ?';
            var sigin_result = await connection.query(sigin_query, user);

            if (sigin_result){
                res.status(200);
                res.json({status: "success", msg: "회원 가입을 성공하였습니다."});
            }
            else {
                res.status(500);
                res.json({status: "success", msg: "회원 가입에 실패하였습니다."});
            }

        }
    }
    else {
        res.status(401);
        res.json({status: "fail", msg: errors});
    }

}catch (err) {
    console.log(err);
    res.status(500).json({status:"fail",msg: err });
}
finally {
    pool.releaseConnection(connection);
}
});

var multiupload = upload.fields([{name:'profile',maxCount:1},{name : "background", maxCount:1}]);
router.post('/modify',multiupload,  async (req, res) => {
    try{
        req.checkHeaders('email', '이메일 입력되지 않았습니다.').notEmpty();
        req.checkHeaders('type', '타입을 입력해주세요.').notEmpty();

        console.log(req.files['profile']);
        console.log(req.files['background']);

        let errors = req.validationErrors();
        if (!errors) {

            var connection = await pool.getConnection();
            await connection.beginTransaction();

            let email = req.headers.email;
            let type = req.headers.type;

            let user={};
            let pen_name = req.body.pen_name;
            let inform = req.body.inform;

            let user_query = 'Select pen_name,profile,background,inform from users where email = ? and foreign_key_type = ?';
            var user_before = await connection.query(user_query, [email,type]);

            var data_img =[];

            var cnt = 0;
            var i = 0;
            if(pen_name){
                let flags = 0;
                if(user_before[0].pen_name){
                    if( user_before[0].pen_name == pen_name)
                        flags = 1;
                }
                if(flags==0){
                    cnt += 1;
                    user.pen_name = pen_name;
                    let check_penname_query = 'Select * from users where pen_name = ?';
                    var check_penname = await connection.query(check_penname_query, pen_name);

                    if (check_penname.length) {
                        res.status(401);
                        res.json({status: "fail", msg: "사용 중인 필명입니다. 다른 필명을 입력해주세요."});
                        return
                    }
                }

            }

            if(inform){
                let flags = 0;
                if(user_before[0].inform){
                    if( user_before[0].inform == inform)
                        flags = 1;
                }
                if(flags==0){
                    cnt += 1;
                    user.inform = inform;
                }
            }

            if(req.files['profile']){
                user.profile = req.files['profile'][0].location;
                await connection.query(s3key_,{location : req.files['profile'][0].location, key_ : req.files['profile'][0].key});
                cnt += 1;

                if(user_before[0].profile){
                    let temp = {};
                    let befor_key = await connection.query(location_key_q_,user_before[0].profile);
                    temp.Key = befor_key[0].key_;
                    data_img[i] =temp;
                    i += 1;
                }
            }
            if(req.files['background']){
                user.background = req.files['background'][0].location;
                await connection.query(s3key_,{location : req.files['background'][0].location, key_ : req.files['background'][0].key});
                cnt += 1;

                if(user_before[0].background){
                    let befor_key2 = await connection.query(location_key_q_,user_before[0].background);
                    let temp2 = {};
                    temp2.Key = befor_key2[0].key_;
                    data_img[i] =temp2;
                }
            }
            if(cnt == 0){
                res.status(201);
                res.json({status: "success", msg: "회원 정보의 변화된 내용이 없습니다."});
            }
            else{
                let update_user_query = 'update seoul_poem.users set ? where email = ? and foreign_key_type = ?';
                var update_user = await connection.query(update_user_query, [user,email, type]);
                if(update_user.protocol41){
                    if(data_img.length){
                        var params = {
                            Bucket: 'csr1994',
                            Delete: { // required
                                Objects: data_img,
                            },
                        };
                        s3.deleteObjects(params, function(err, data) {
                            if (err) console.log(err, err.stack); // an error occurred
                            else     console.log(data);           // successful response
                        });
                    }
                    res.status(200);
                    res.json({status: "success", msg: "회정 정보 수정 성공", user : user});
                }else {
                    res.status(500);
                    res.json({status: "fail", msg: "회원 정보 수정 실패"});
                }
            }
        }
        else{
            res.status(401);
            res.json({status: "fail", msg: errors});
        }
    }catch (err) {
        console.log(err);
        res.status(500).json({status:"fail",msg: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});




module.exports = router;
