var express = require('express');
var router = express.Router();
const pool = require('../config/db_pool');
const jwt = require('jsonwebtoken');


/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond with a resource');
});


module.exports = router;
