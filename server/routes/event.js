const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');
const s3 = new aws.S3();
const { Iamporter, IamporterError } = require('iamporter');
// For Production
const iamporter = new Iamporter({
  apiKey: '9236392721598164',
  secret: 'SxPvT4EOn0rI0HqeRYdI4Q8bzHG2vkonRdTLiyzFy1nyIONzM1iQRr2q9JsE4Q1V4UHnsg21iqA0enA0'
});

const upload = multer({
  storage: multerS3({
    s3: s3,
    bucket: 'sopt20.server.seminar6',
    acl: 'public-read',
    key: function (req, file, cb) {
      cb(null, Date.now() + '.' + file.originalname.split('.').pop());
    }
  })
});



//메인화면 최근행사 더보기
router.get('/', async (req, res) => {
    try {

        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

        if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
        else {
        var decoded_pk = jwt.decode(token, {complete: true});

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query = 'SELECT event.id as event_id, event.title as event_title, managerNa_aj.group.title as group_title, event.start_date, event.place, event.amount, event.photo FROM managerNa_aj.event ,  managerNa_aj.group WHERE group_id IN ( SELECT group_id FROM managerNa_aj.group_has_user, managerNa_aj.group, managerNa_aj.user WHERE managerNa_aj.user.pk = ? and managerNa_aj.group_has_user.user_id = managerNa_aj.user.pk and managerNa_aj.group.id =  managerNa_aj.group_has_user.group_id) and managerNa_aj.group.id = event.group_id';
        let event = await connection.query(query, decoded_pk.payload.pk);


        res.status(200).send(  { event: event  });
        await connection.commit();
        }

    }
    catch(err){
        console.log(err);
        res.status(500).send( { result : err });
        connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }

});



//1개의 행사 상세 정보 보여주기
router.get('/:event_id', async (req, res) => {
    try {

              var connection = await pool.getConnection();
              await connection.beginTransaction();

        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

        if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
        else {
        var decoded_pk = jwt.decode(token, {complete: true});


        let query1 = 'select * from managerNa_aj.event where id = ?';
        let event_info = await connection.query(query1, req.params.event_id) || null;
        if(event_info.length==0) res.status(403).send({result: '존재하지 않는 행사 id입니다.'});



        let guery1_1 = 'select managerNa_aj.group.title, managerNa_aj.group.photo, managerNa_aj.group.id from managerNa_aj.event, managerNa_aj.group where event.id = ? and event.group_id = managerNa_aj.group.id;'
        let group_info = await connection.query(guery1_1, req.params.event_id) || null;

        let guery1_2 = 'SELECT event.title, event.text, event.place, event.start_date, event.manager_name, event.end_date, event.photo, event.amount From event Where  event.id  = ? ;'
        let event_info_1 = await connection.query(guery1_2, req.params.event_id) || null;


        var is_manager = 0;
        var is_participated = 0;
        var is_paid = 0;

        let guery1_3 = 'SELECT managerNa_aj.user_has_event.is_manager From managerNa_aj.user_has_event, managerNa_aj.event Where  managerNa_aj.user_has_event.user_pk  = ? and managerNa_aj.event.id =  managerNa_aj.user_has_event.event_id and managerNa_aj.event.id = ?;'
        let event_info_2 = await connection.query(guery1_3, [decoded_pk.payload.pk, req.params.event_id]);


        console.log(event_info_2[0]);

        if(event_info_2.length==0) {
          is_manager = 0;
          is_participated = 0;
        }
        else {
          is_manager = event_info_2[0].is_manager;
          is_participated = 1;

          let query = 'select deposit_status from managerNa_aj.user_has_event where event_id = ? and user_pk =? and deposit_status = 1';
          let paid_info = await connection.query(query, [req.params.event_id, decoded_pk.payload.pk]) || null;

          if(paid_info.length==0)  is_paid = 0;
          else is_paid = paid_info[0].deposit_status;

        }



        let query2 = 'SELECT managerNa_aj.group.title as group_title, managerNa_aj.group.photo as group_photo, event.title, event.text, event.place, event.start_date, event.manager_name , event.end_date, event.photo, managerNa_aj.user_has_event.is_manager ,event.amount FROM managerNa_aj.event, managerNa_aj.group, managerNa_aj.user_has_event WHERE managerNa_aj.user_has_event.user_pk = ? and managerNa_aj.user_has_event.event_id = managerNa_aj.event.id and managerNa_aj.group.id = event.group_id and event.id = ?';
        let event = await connection.query(query2, [decoded_pk.payload.pk, req.params.event_id]);
        //if(event.length==0) res.status(406).send({result: '참여하지 않은 그룹의 행사입니다.'});


        let event_info_response = {
          "group_id": group_info[0].id,
          "group_title": group_info[0].title,
          "group_photo": group_info[0].photo,
          "title": event_info_1[0].title,
          "text": event_info_1[0].text,
          "place": event_info_1[0].place,
          "manager_name": event_info_1[0].manager_name,
          "start_date": event_info_1[0].start_date,
          "end_date": event_info_1[0].end_date,
          "photo": event_info_1[0].photo,
          "amount": event_info_1[0].amount,
          "is_manager": is_manager,
          "is_participated" : is_participated,
          "is_paid" : is_paid

        };


        let query3 = 'SELECT event_count, place, price FROM managerNa_aj.sub_event where event_id = ?;'
        let sub_event = await connection.query(query3, req.params.event_id);


        let query4 = 'Select user_event_1,user_event_2,user_event_3 From managerNa_aj.user_has_event where user_has_event.user_pk = ? and user_has_event.event_id = ?'
        let sub_event_participate = await connection.query(query4, [decoded_pk.payload.pk, req.params.event_id]);

        if (event_info_response.is_participated == 1){
          if (sub_event.length == 0) console.log("행사내용 없음");
          else if (sub_event.length == 1) {
             sub_event[0]['is_participated'] = '1';
          }
          else if (sub_event.length == 2) {
             sub_event[0]['is_participated'] = '1';
            if(sub_event_participate[0].user_event_1==1)   sub_event[1]['is_participated'] = '1';
            else sub_event[1]['is_participated'] = '0';
          }
          else if (sub_event.length == 3) {
             sub_event[0]['is_participated'] = '1';
            if(sub_event_participate[0].user_event_1==1)   sub_event[1]['is_participated'] = '1';
            else sub_event[1]['is_participated'] = '0';

            if(sub_event_participate[0].user_event_2==1)   sub_event[2]['is_participated'] = '1';
            else sub_event[2]['is_participated'] = '0';
          }
          else if (sub_event.length == 4) {
             sub_event[0]['is_participated'] = '1';
            if(sub_event_participate[0].user_event_1==1)   sub_event[1]['is_participated'] = '1';
            else sub_event[1]['is_participated'] = '0';

            if(sub_event_participate[0].user_event_2==1)   sub_event[2]['is_participated'] = '1';
            else sub_event[2]['is_participated'] = '0';

            if(sub_event_participate[0].user_event_3==1)   sub_event[3]['is_participated'] = '1';
            else sub_event[3]['is_participated'] = '0';
          }
        }




        res.status(200).send( { event: event_info_response , sub_event: sub_event });
        await connection.commit();
        }

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

// 행사 만들기
router.post('/:group_id',upload.single('photo'), async (req, res, next) => {
  try {
      let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
      let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

      if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
      else {
      var decoded_pk = jwt.decode(token, {complete: true});

      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let query = 'select * from managerNa_aj.group where id = ?';
      let group_info = await connection.query(query, req.params.group_id) || null;
      if(group_info.length==0) res.status(403).send({result: '존재하지 않는 그룹 id입니다.'});


      let event = {
        title: req.body.title,
        text: req.body.text,
        place: req.body.place,
        amount: req.body.amount,
        group_id : req.params.group_id,
        start_date: req.body.start_date,
        end_date: req.body.end_date,
        manager_name: req.body.manager_name,
        photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
      };

      let query1 = 'insert into managerNa_aj.event set ? ';
      let data =  await connection.query(query1, event);

      let event_1st = {
        event_count : 1,
        event_id : data.insertId,
        event_group_id: req.params.group_id,
        place: req.body.place_1st,
        price: req.body.amount_1st
      };

      let query2 = 'insert into managerNa_aj.sub_event set ? ';
      // await connection.query(query2, event_1st);

      let event_2nd = {
        event_count : 2,
        event_id : data.insertId,
        event_group_id: req.params.group_id,
        place: req.body.place_2nd,
        price: req.body.amount_2nd
      };

    //  await connection.query(query2, event_2nd);


      let event_3rd = {
        event_count : 3,
        event_id : data.insertId,
        event_group_id: req.params.group_id,
        place: req.body.place_3rd,
        price: req.body.amount_3rd
      };
      // await connection.query(query2, event_3rd);

      let user_has_event = {
        user_pk : decoded_pk.payload.pk,
        event_id : data.insertId,
        deposit_status: 0,
        sum_amount: 0, //
        is_manager: 1
      };


      let query3 = 'insert into managerNa_aj.user_has_event set ? ';
      // await connection.query(query3, user_has_event);

      let data1 = req.body.place_1st;
      let data2 = req.body.place_2nd;
      let data3 = req.body.place_3rd;
      if(data1 && (!data2) && (!data3)) await connection.query(query2, event_1st);
      else if((!data1) && (data2) && (!data3)) await connection.query(query2, event_2nd);
      else if((!data1) && (!data2) && (data3)) await connection.query(query2, event_3rd);
      else if((data1) && (data2) && (!data3)){
        await connection.query(query2, event_1st);
        await connection.query(query2, event_2nd);
      }
      else if((!data1) && (data2) && (data3)){
        await connection.query(query2, event_2nd);
        await connection.query(query2, event_3rd);
      }
      else if((data1) && (!data2) &&(data3)){
        await connection.query(query2, event_1st);
        await connection.query(query2, event_3rd);
      }
      else if((data1) && (data3) && (data3)){
        await connection.query(query2, event_1st);
        await connection.query(query2, event_2nd);
        await connection.query(query2, event_3rd);
      }


     await connection.query(query3, user_has_event);


      res.status(201).send({result: "add group success"});
      await connection.commit();
      }
    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// 행사 정보 수정
router.put('/:group_id/:event_id',upload.single('photo'), async (req, res, next) => {
  try {
        var connection = await pool.getConnection();
        let event_id = req.params.event_id;
        let group_id = req.params.group_id;

        let event_1st = {
          event_count : 1,
        //  event_id : event_id,
        //  event_group_id: req.params.group_id,
          place: req.body.place_1st,
          price: req.body.amount_1st
        };

        let query2 = 'update managerNa_aj.sub_event set ? where event_id = ? and event_group_id = ? and event_count = 1';
        let data = await connection.query(query2, [event_1st, event_id, group_id]);

        let event_2nd = {
          event_count : 2,
          place: req.body.place_2nd,
          price: req.body.amount_2nd
        };

        let query3 = 'update managerNa_aj.sub_event set ? where event_id = ? and event_group_id = ? and event_count = 2';
        await connection.query(query3, [event_2nd, event_id, group_id]);

        let event_3rd = {
          event_count : 3,
          place: req.body.place_3rd,
          price: req.body.amount_3rd
        };
        let query4 = 'update managerNa_aj.sub_event set ? where event_id = ? and event_group_id = ? and event_count = 3';
        await connection.query(query4, [event_3rd, event_id, group_id]);

        let event = {
          title: req.body.title,
          text: req.body.text,
          place: req.body.place,
          amount: req.body.amount,
          start_date: req.body.start_date,
          end_date: req.body.end_date,
          group_id: req.params.group_id,
          photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
        };

        let query = 'update managerNa_aj.event set ? where id = ?';
        await connection.query(query, [event, event_id]);

        res.status(201).send({result: "update event success"});

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// delete /event/:event_id : 파라미터로 들어온 id 값을 가진 모임 정보 삭제
router.delete('/:event_id', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let query = 'SELECT * FROM managerNa_aj.event where id=? ';
      let data1 = await connection.query(query, req.params.event_id) || null;
      if(data1.length==0) res.status(403).send({result: '존재하지 않는 행사 id입니다.'});

      let query1 = 'SELECT * FROM managerNa_aj.sub_event where event_id=? ';
      let data2 = await connection.query(query1, req.params.event_id) || null;
      if(data2.length>0){
        let query2 = 'delete from managerNa_aj.sub_event where event_id = ?';
        await connection.query(query2, req.params.event_id);
      }

      let query3 = 'delete from managerNa_aj.user_has_event where event_id = ?';
      await connection.query(query3, req.params.event_id);

      let query3_1 = 'delete from managerNa_aj.event where id = ?';
      await connection.query(query3_1, req.params.event_id);

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

//비회원 리스트 추가  (여러명)
router.post('/:event_id/participant',function(req,res,next){//전화번호 동기화 기능
  pool.getConnection(function(error,connection){
    if(error){
      console.log("getConnection Error"+error);
      res.sendStatus(500);
    }
    else
    {
      function repeater(i){//node는 비동기화이기때문에 반복문을 사용하기위해 재귀함수를 이용함
        if(i<req.body.not_users.length)//받아온 친구들의 길이 많큼 재귀적으로 돌림
        {

          let not_user = {
            name : req.body.not_users[i].name,
            ph : req.body.not_users[i].ph
          };
        connection.query('insert into managerNa_aj.user set ? ',not_user,function(error,row){
            if(error){
              console.log("connection error"+error);
              res.status(500).send({result:'FAIL'});
            }
            else{
                let not_users_has_event = {
                  user_pk : row.insertId,
                  event_id : req.params.event_id,
                  deposit_status : req.body.deposit_status,
                  sum_amount : req.body.not_users[i].amount,
                  is_manager : 0,
                }

                connection.query('insert ignore into managerNa_aj.user_has_event set ? ',not_users_has_event, function (err,result){

                  if(err){
                    console.log('insert query err', err);
                    res.status(400).send({result:'insert query err'});
                    connection.release();
                  }
                  else{
                      console.log('insert second query success');
                  }
                });

            }
            repeater(i+1);
          });
          }
        if(i == req.body.not_users.length)//반복문이 끝났을 시
        {


          res.status(200).send({result:'SYNC SUCCESS'});//success보냄
          connection.release();
        }
      }
      repeater(0);
    }
  });
});



//참가자 명단 보기
router.get('/:event_id/participant', async (req, res) => {
    try {

        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

        if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
        else {
        var decoded_pk = jwt.decode(token, {complete: true});

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1_1 = 'SELECT user.pk as user_pk, user.name, user_has_event.sum_amount as amount, user.ph FROM managerNa_aj.user, managerNa_aj.user_has_event WHERE event_id = ? and user.pk = user_has_event.user_pk and managerNa_aj.user_has_event.deposit_status = 1;';
        let paid_list = await connection.query(query1_1, req.params.event_id);

        let query1_2 = 'SELECT user.pk as user_pk, user.name, user_has_event.sum_amount as amount, user.ph FROM managerNa_aj.user, managerNa_aj.user_has_event WHERE event_id = ? and user.pk = user_has_event.user_pk and managerNa_aj.user_has_event.deposit_status = 0;';
        let unpaid_list = await connection.query(query1_2, req.params.event_id);


        let query2 = 'SELECT count(user.pk) as parti_count FROM managerNa_aj.user, managerNa_aj.user_has_event WHERE event_id = ? and user.pk = user_has_event.user_pk';
        let participant_cnt = await connection.query(query2, req.params.event_id);

        let query3 = 'SELECT count(user.pk) as deposit_1_count FROM managerNa_aj.user, managerNa_aj.user_has_event WHERE event_id = ? and user.pk = user_has_event.user_pk and deposit_status = 1';
        let deposit_1 = await connection.query(query3, req.params.event_id);

        let query4 = 'SELECT count(user.pk)  as deposit_0_count FROM managerNa_aj.user, managerNa_aj.user_has_event WHERE event_id = ? and user.pk = user_has_event.user_pk and deposit_status = 0';
        let deposit_0 = await connection.query(query4, req.params.event_id);

        res.status(200).send( {  paid_list: paid_list, unpaid_list: unpaid_list , participant_count: participant_cnt[0].parti_count, paid_count: deposit_1[0].deposit_1_count, unpaid_count:deposit_0[0].deposit_0_count });
        await connection.commit();
        }

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



// 입금자-미입금자로 보내기(총무용)
// /event/{event_id}/participant/move
router.put('/:event_id/participant/move', async (req, res, next) => {
  pool.getConnection(function(error,connection){
    if(error){
      console.log("getConnection Error"+error);
      res.sendStatus(500);
    }
    else
    {
      function repeater(i){//node는 비동기화이기때문에 반복문을 사용하기위해 재귀함수를 이용함
        if(i<req.body.user_list.length)//받아온 친구들의 길이 많큼 재귀적으로 돌림
        {

          let user_list = {
            user_pk : req.body.user_list[i].user_pk,
            deposit_status : req.body.target_deposit_status
          };
            connection.query('update managerNa_aj.user_has_event set managerNa_aj.user_has_event.deposit_status = ? where managerNa_aj.user_has_event.user_pk = ? and event_id =?',[user_list.deposit_status,user_list.user_pk,req.params.event_id],function(error,row){
            if(error){
              console.log("connection error"+error);
              res.status(500).send({result:'FAIL'});
            }
            else{
              console.log(user_list.user_pk+"'s status update to 1");
            }
            repeater(i+1);
          });
        }
        if(i == req.body.user_list.length)//반복문이 끝났을 시
        {
              res.status(200).send({result:'SYNC SUCCESS'});//success보냄
              connection.release();
        }
      }
      repeater(0);
    }
  });
});


//참가하기
//event/{event_id}/apply
router.post('/:event_id/apply', async (req, res, next) => {
  try {
    let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
    let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

    if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
    else {
    var decoded_pk = jwt.decode(token, {complete: true});
        var connection = await pool.getConnection();

        let apply = {
          user_pk: decoded_pk.payload.pk,
          event_id: req.params.event_id,
          deposit_status: 0,
          is_manager: 0,
          user_event_1 : req.body.user_event_1,
          user_event_2 : req.body.user_event_2,
          user_event_3 : req.body.user_event_3,
          sum_amount : req.body.sum_amount

        };

        console.log(decoded_pk.payload.pk);
      let query = 'select * from  managerNa_aj.user_has_event where user_has_event.event_id = ? and user_has_event.user_pk = ?'; //디비에 이미 존재하는 이메일주소가 있는지 조회합니다.
      let data = await connection.query(query, [req.params.event_id,decoded_pk.payload.pk]);
      if(data.length>0) res.status(406).send({result: '이미 참여한 행사입니다.'});
      else {
        let query1 = 'insert into managerNa_aj.user_has_event set ?';
        let inserted1 = await connection.query(query1, apply);
        console.log(inserted1);

        res.status(201).send({result: "apply event success"});
        }
      }

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

//참가 취소하기
//event/{event_id}/apply
router.delete('/:event_id/apply', async (req, res, next) => {
  try {
    let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
    let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

    if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
    else {
    var decoded_pk = jwt.decode(token, {complete: true});
        var connection = await pool.getConnection();

      let query = 'SELECT * FROM managerNa_aj.user_has_event where user_pk=? and event_id=? ';
      let data1 = await connection.query(query, [decoded_pk.payload.pk,req.params.event_id]) || null;
      if(data1.length==0) res.status(403).send({result: '존재하지 않는 행사이거나 참가하지 않은 행사입니다.'});

      let query1 = 'delete from managerNa_aj.user_has_event where user_pk=? and event_id = ?';
      await connection.query(query1, [decoded_pk.payload.pk,req.params.event_id]);

      res.status(201).send({result: "delete apply success"});

      }

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

//결제하기
//event/{event_id}
router.put('/:event_id', async (req, res, next) => {
  try {
    let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
    let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

    if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
    else {
    var decoded_pk = jwt.decode(token, {complete: true});
        var connection = await pool.getConnection();

        let query = 'SELECT * FROM event where id=? ';
        let data1 = await connection.query(query, req.params.event_id) || null;
        if(data1.length==0) res.status(403).send({result: '존재하지 않는 행사 id입니다.'});

        let query1 = 'SELECT * FROM user_has_event where deposit_status = 1 and user_pk = ? and event_id=? ';
        let data2 = await connection.query(query1, [decoded_pk.payload.pk, req.params.event_id]) || null;
        if(data2.length>0) res.status(406).send({result: '이미 결제했습니다.'});

        let query2 = 'update managerNa_aj.user_has_event set managerNa_aj.user_has_event.deposit_status = 1 where managerNa_aj.user_has_event.user_pk = ? and event_id =?';
        let updated = await connection.query(query2, [decoded_pk.payload.pk, req.params.event_id]);

        res.status(201).send({result: "update payment success"});

      }

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
