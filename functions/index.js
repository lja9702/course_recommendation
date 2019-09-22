
//TODO 4: recommendation을 해주는 함수 만들어서 해당 사용자가 요청할 때 추천해주기
//TODO 5: store가 식당인지 카페인지로 구별해서 만들기

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();
//db 가져오기
var db = admin.database();
var ref = db.ref("favorite");
let dbDict = {}; //db Dictionary key: user_id value: store_array
let sigDict = {}; //row: user_id value: signiture array
let secIndex = {};//secondary Index
const SIGSIZE = 10;

//db가 업데이트 될때 갱신하는 함수
exports.getDBData = functions.https.onCall((data, context) => {
  ref.on("value", function(snapshot) {
    dbDict = snapshot.val();
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });

  console.log(dbDict);
  console.log(sigDict);
  console.log(secIndex);

  signiture();
  makeSecondaryIndex();

  return dbDict;
});

//Database에서 가져온 정보 저장하는 배열_ row: user_id, list: store_id

// Take the text parameter passed to this HTTP endpoint and insert it into the
// 안드로이드가 서버로 유저정보, 가게정보 순으로 학습 요청
exports.dataInfo = functions.https.onCall((data, context) => {
  //user_id, store_id라는 파라미터를 가져옴
  const user_id = data.user_id;
  const store_id = data.store_id;
  return {user_id: user_id,
          store_id: store_id};
});

exports.Recommendation = functions.https.onCall((data, context) => {
  //user_id, store_id라는 파라미터를 가져옴
  const user_id = data.user_id;
  //가장 비슷한 선호도를 지니는 user추천
  let syncSignitureUser = {};
  for(var idx = 0;idx < SIGSIZE;idx++){
    var key = String(sigDict[user_id][idx]) + "-" + String(idx);
    for(var similar_user in sec_Index[key])
      syncSignitureUser[similar_user]++;
  }
  let mostSyncUser = 0, maxCnt = 0;
  for(user in syncSignitureUser){
    if(user === user_id) continue;
    if(maxCnt < syncSignitureUser[user]){
      maxCnt = syncSignitureUser[user];
      mostSyncUser = user;
    }
  }

  return {recomStoreList: dbDict[mostSyncUser].filter(x => !dbDict[user_id].includes(x))};
});

// min_Hash 값을 return 하는 함수
let minHash = function(a_index, user_id){
  const MOD = 11;
  let res = MOD; //store_id가 10이라고 가정
  let prime = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29];

  for(var store_id in dbDict[user_id])
    res = Math.min(res, (prime[a_index] * dbDict[user_id][store_id] + 2 * a_index) % MOD);

  return res;
}
//signiture를 만드는 함수 (min_Hash들의 집합)
var signiture = function(){
  for(var user_id in dbDict){
    sigDict[user_id] = [];
    for(var a_index = 0;a_index < SIGSIZE;a_index++){
      let sig = minHash(a_index, user_id);
      sigDict[user_id].push(sig);
    }
  }
}
//Secondary index를 만들어 "#signiture값-index값"를 키로하고 user_id들의 array를 value로 함
var makeSecondaryIndex = function(){
  for(var user_id in sigDict){
    for(var a_index = 0;a_index < SIGSIZE;a_index++){
      var key = String(sigDict[user_id][a_index]) + "-" + String(a_index);
      if(!(key in secIndex)) secIndex[key] = [];
      secIndex[key].push(user_id);
    }
  }
}
