var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var jwt = require('jsonwebtoken');
var index = require('./routes/index');
var users = require('./routes/users');
var config = require('./config/secretKey'); //보안
var main = require('./routes/main');
var mypage = require('./routes/mypage');
var article = require('./routes/article');
var bookmark = require('./routes/bookmark');
var subway = require('./routes/subway');
var author = require('./routes/author');
var notice = require('./routes/notice');
var expressVaildator = require('express-validator');


var app = express();
// view engine setup
app.set('views', path.join(__dirname, 'vi   ews'));
app.set('view engine', 'ejs');
app.set('jwt-secret', config.secret); //시크릿

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));

app.use(expressVaildator());
app.use(logger('dev'));
app.use(bodyParser.urlencoded({limit: '10mb', extended: false }));
app.use(bodyParser.json({limit: '10mb'}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, '../public')));

app.use(function (req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
    res.header('Access-Control-Allow-Headers', 'content-type, x-access-token'); //1
    next();
});

app.use('/', index);
app.use('/users', users);
app.use('/main', main);
app.use('/article', article);
app.use('/bookmark', bookmark);
app.use('/subway', subway);
app.use('/mypage', mypage);
app.use('/author', author);
app.use('/notice',notice);
app.disable('etag');


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
