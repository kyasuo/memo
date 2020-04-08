
// 期間(秒)
const duration = 10;
// 対象要素
const elem = document.querySelector("#menu_content > div:nth-child(1) > div:nth-child(3)"); // TODO
// 監視内容
var observer = new MutationObserver(function(){
  var msg = "dom was changed." // TODO
  outputLog(msg);
});
// 設定
const config = { 
  attributes: true, 
  childList: true, 
  characterData: true,
  subtree: true
};

// -- 関数定義
var outputLog = function( msg ) {
  console.log(currentDateTimeString() + " " + msg);
};

var currentDateTimeString = function () {
  var d = new Date();
  var zeroPad = function(number, len) {
    return ("0" + number).slice(-1*len);
  }
  var year = d.getFullYear();
  var month = zeroPad(d.getMonth()+1, 2);
  var date = zeroPad(d.getDate(), 2);
  var hour = zeroPad(d.getHours(), 2);
  var min = zeroPad(d.getMinutes(), 2);
  var sec = zeroPad(d.getSeconds(), 2);
  var msec = zeroPad(d.getMilliseconds(), 3);
  return "[" + year + "/" + month + "/" + date + " " + hour + ":" + min + ":" + sec + "." + msec + "]";
};

var startObserving = function() {
 outputLog("start");
  observer.observe(elem, config);
  setTimeout(function() {
    observer.disconnect();
    outputLog("finish");
  }, duration * 1000);
};
