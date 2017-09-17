const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool');
const jwt = require('jsonwebtoken');

/*promise-mysql로 메인화면에서 id가 속한 모임의 최근 행사들과 내가 참여한 모임 보여주기*/
router.get('/', async (req, res) => {
    try {

        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        // get the decoded payload and header
        var decoded = jwt.decode(token, {complete: true});
        console.log( decoded.payload.pk); //토큰에서 pk 받아오기 ~~~~~

        var connection = await pool.getConnection();
        await connection.beginTransaction();
        // let query1 = 'update post set view_number = view_number + 1 where id = ?';
        // await connection.query(query1, req.params.id);
        let query1 = 'SELECT event.id, event.title as event_title, managerNa_aj.group.title as group_title ,event.text, event.place, event.start_date, event.photo FROM managerNa_aj.event, managerNa_aj.group WHERE group_id IN ( SELECT group_id FROM managerNa_aj.group_has_user, managerNa_aj.group, managerNa_aj.user WHERE managerNa_aj.user.pk = ? and managerNa_aj.group_has_user.user_id = managerNa_aj.user.pk and managerNa_aj.group.id =  managerNa_aj.group_has_user.group_id) and managerNa_aj.group.id = managerNa_aj.event.group_id order by start_date desc limit 5';
        let events = await connection.query(query1, decoded.payload.pk);
        let query2 = 'SELECT managerNa_aj.group.id, managerNa_aj.group.title, managerNa_aj.group.photo, group_has_user.is_new, group_has_user.is_chairman FROM managerNa_aj.group_has_user, managerNa_aj.group,managerNa_aj.user WHERE managerNa_aj.user.pk = ? and managerNa_aj.group.id =  managerNa_aj.group_has_user.group_id and managerNa_aj.group_has_user.user_id = managerNa_aj.user.pk order by title limit 17;';

        let groups = await connection.query(query2, decoded.payload.pk);



        res.status(200).send(  { events: events, groups: groups });
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

//마이페이지 조회
router.get('/mypage', async (req, res) => {
    try {

        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
        var decoded_pk = jwt.decode(token, {complete: true});
        if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
        else {
         var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT name, email, photo, ph  FROM managerNa_aj.user where user.pk= ?';
        let my_profile = await connection.query(query1, decoded_pk.payload.pk);
        let query2 = 'SELECT event.id, event.group_id, event.photo, managerNa_aj.group.title as group_title, event.title as event_title, event.start_date, user_has_event.deposit_status FROM managerNa_aj.event , managerNa_aj.group, managerNa_aj.user , managerNa_aj.user_has_event WHERE managerNa_aj.user.pk = ? and managerNa_aj.group.id = managerNa_aj.event.group_id and managerNa_aj.user.pk =  managerNa_aj.user_has_event.user_pk and managerNa_aj.user_has_event.event_id = managerNa_aj.event.id';
        let participate_in = await connection.query(query2, decoded_pk.payload.pk);

        let query3 = 'select event.id, event.group_id, event.title as event_title, event.photo, event.start_date, managerNa_aj.group.title as group_title from managerNa_aj.event, managerNa_aj.group where event.id in (select user_has_event.event_id from managerNa_aj.user_has_event where user_has_event.user_pk = ? and user_has_event.is_manager = 1) and managerNa_aj.group.id = event.group_id ;';
        let my_event = await connection.query(query3, decoded_pk.payload.pk);

        let query4 = 'select managerNa_aj.group.id, managerNa_aj.group.photo, managerNa_aj.group.title from managerNa_aj.group where id in (select group_id from group_has_user, user where group_has_user.is_chairman = 1 and user.pk = ? and user.pk= group_has_user.user_id)';
        let my_group = await connection.query(query4, decoded_pk.payload.pk);


        function repeater(i){//node는 비동기화이기때문에 반복문을 사용하기위해 재귀함수를 이용함
          if(i<my_event.length)//받아온 행사길이 만큼 재귀 돌려버림~
          {


            connection.query('select count(user_pk) as count from managerNa_aj.user_has_event where deposit_status = 0 and user_has_event.event_id = ?;', my_event[i]['id'] ,function(error,row){
            if(error){
              console.log("connection error"+error);
              res.status(500).send({result:'FAIL'});
            }
            else{
              my_event[i]['count'] = row[0].count
            }
            repeater(i+1);
          });
          }
          if(i == my_event.length)//반복문이 끝났을 시
          {
              res.status(200).send( { my_profile: my_profile[0], participate_in: participate_in, my_event:my_event, my_group:my_group  });
          }
        }
        repeater(0);





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


module.exports = router;
