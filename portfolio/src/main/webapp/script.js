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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function deleteGreetings() {
  fetch('/data', {method: 'DELETE'});
}

/* Add greetings with translation. */
function getGreetingsInLanguage() { 
  var language = document.getElementById('languages').value;
  getGreetings(language);
}

/* Add all fetched greetings to the page. */
function getGreetings(language) {
  document.getElementById('greeting-container').innerText = "";
  fetch('/data?' + new URLSearchParams({
    languages: language,
})).then(response => response.json()).then(greeting => greeting.forEach(addGreeting));
}

/* Add value of greeting field to the page. */
function addGreeting(greeting) {
  const safeGreeting = greeting.greeting.replace(/</g, "&lt;").replace(/>/g, "&gt;");
  document.getElementById('greeting-container').innerText += safeGreeting + "\n";
}
