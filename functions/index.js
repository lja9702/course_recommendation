//TODO 1: 클라이언트(안드로이드)로 부터 가게 정보, 유저 정보 받기

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();


// Take the text parameter passed to this HTTP endpoint and insert it into the
// 안드로이드가 서버로 유저정보, 가게정보 순으로 학습 요청
exports.dataInfo = functions.https.onCall((data, context) => {
  //user_id, store_id라는 파라미터를 가져옴

  const user_id = data.user_id;
  const store_id = data.store_id;

  return {user_id: user_id,
          store_id: store_id};
});
