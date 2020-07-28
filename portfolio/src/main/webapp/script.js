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
  fetch('/data', {method: 'DELETE'});
}

/* Add comments with translation. */
function getCommentsInLanguage() { 
  var language = document.getElementById('languages').value;
  getComments(language);
}

/* Add all fetched comments to the page. */
function getComments(language) {
  document.getElementById('comment-container').innerText = "";
  fetch('/data?' + new URLSearchParams({
    languages: language,
})).then(response => response.json()).then(comment => comment.forEach(addComment));
}

/* Add value of comment field to the page. */
function addComment(comment) {
  const safeComment = safeEncoding(comment.comment);
  document.getElementById('comment-container').innerText += safeComment + "\n";
}

/* Replace symbols in text for safety. */
function safeEncoding(text) {
  return text.replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
