// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/* Remove all comments. */
function deleteComments() {
  fetch('/comment', {method: 'DELETE'});
}

/* Add comments with translation. */
function getCommentsInLanguage() { 
  var language = document.getElementById('language').value;
  getComments(language);
}

/* Add all fetched comments to the page. */
function getComments(language) {
  document.getElementById('comment-container').innerText = "";
  fetch('/comment?' + new URLSearchParams({
    language: language,
})).then(response => response.json()).then(comments => comments.forEach(addComment));
}

/* Add value and image of comment fields to the page. */
function addComment(comment) {
  const safeComment = safeEncoding(comment.comment);
  document.getElementById('comment-container').innerHTML += "<p>" + safeComment + "</p><br>";
  document.getElementById('comment-container').innerHTML += "<a href=\"" + comment.imageUrl + "\"><img src=\"" + comment.imageUrl + "\" /></a><br><br>";
}

/* Replace symbols in text for safety. */
function safeEncoding(text) {
  return text.replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

/* Fetch Blobstore URL. */
function fetchBlobstoreUrl() {
  fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('my-form');
        messageForm.action = imageUploadUrl;
      });
}
