// ********* Fetch GRE word details by name (on button click) // *********
function callGetWordDetailsApi(){
const word = document.getElementById("greWordInput").value;

console.log("Sending word: ", word);

fetch('/getGreWordDetailsByName',{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json', // Ensure it sets response type
        },
        body: JSON.stringify({
        name: word,
        databaseType: "dynamodb"
        })
})
.then(response => {
if(!response.ok){
throw new Error("Please check the GRE word.");
}
return response.json();
})
.then(data =>{
console.log("API response data: ", data);
console.log("Extracted Result object:", data.Result);

const definition = data.Result?.Explanation || "No definition found.";
const example = data.Result?.Training_English_Word  || "No example found.";

console.log("Definition:", definition);
console.log("Example:", example);

document.getElementById("greWordDefinition").value = definition;
document.getElementById("greWordExample").value = example;
})
.catch(error => {
console.error("Fetch error: ", error);
document.getElementById("greWordDefinition").value = `Error: ${error.message}`;
//document.getElementById("greWordExample").value = '';
});
}



// ********* On page load, fill the existing input with least viewed GRE word and details *********
function loadLeastViewedWord(){
fetch('/getGreWordDetailsByViewCount',
{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
         },
          body: JSON.stringify({
                databaseType: "dynamodb"
          })
        })
.then(response =>{
if(!response.ok){
throw new Error("Failed to fetch least viewed Gre word details.")
}
return response.json();
})
.then(data =>{
console.log("Least viewed Gre word data ", data);

const firstItem = data.Result?.[0]; //Accessing the first item in the array

const word = firstItem?.Training_English_Word  || "Not available";
const definition = firstItem?.Explanation || "Not available";

document.getElementById("greWordInput").value = word;
document.getElementById("greWordDefinition").value = definition;
})
.catch(error =>{
console.error("Error loading the Gre word details. ", error);
document.getElementById("greWordInput").value = "Error";
document.getElementById("greWordDefinition").value = "Error";
});
}




// ********* Function to get all GRE word records *********
function callGetAllWordRecordsApi(){
console.log("Fetching all the GRE word records...");

fetch('/getAllRecords',{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json', // Ensure it sets response type
        },
        body: JSON.stringify({
        databaseType: "dynamodb"
        })
})
.then(response => {
if(!response.ok){
throw new Error("Failed to fetch GRE word records.");
}
return response.json();
})
.then(data =>{
console.log("API response data: ", data);

const resultsContainer= document.getElementById("allGreWordContainer");
resultsContainer.innerHTML = ""; //clearing the previous data/content

const records = data.Result || [];

if(records.length === 0){
resultsContainer.innerHTML = "<p>No records found.</p>";
return;
}

// ********* Display only GRE word and explanation columns *********
const displayFields = [
{key: "Training_English_Word", label: "GRE Word"},
{key: "Explanation", label: "Definition"}
];

//Extracting all column keys
//const allKeys =Object.keys(records[0]);

//Creating table and headers
const table = document.createElement("table");
table.border = "1";
table.style.borderCollapse = "collapse";
table.style.width = "100%";

const thead = table.createTHead();
const headerRow = thead.insertRow();

displayFields.forEach(field =>{
const th = document.createElement("th");
th.textContent = field.label;
th.style.padding = "9px";
th.style.backgroundColor = "#f2f2f2";
headerRow.appendChild(th);
});

//Adding rows for each record
const tbody = table.createTBody();

records.forEach(record => {
const row = tbody.insertRow();
displayFields.forEach(field => {
const cell =row.insertCell();
cell.textContent = record[field.key] ?? "";
cell.style.padding = "9px";
});
});

resultsContainer.appendChild(table);
})
.catch(error => {
console.error("Fetch error: ", error);
document.getElementById("allGreWordContainer").innerHTML = `<p style="color:red;">Error: ${error.message}</p>`;
//document.getElementById("greWordExample").value = '';
});
}

// ********* Function to submit new GRE word *********
function callSubmitGREWordDetailsApi(){

fetch('/postGreWord',{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json', // Ensure it sets response type
        },
        body: JSON.stringify({
        name: word,
        definition: meaning,
        databaseType: "dynamodb"
        })
})
.then(response => {
if(!response.ok){
throw new Error("Failed to post new GRE word.");
}
return response.json();
})
.then(data =>{

const definition = data.Result?.Explanation || "No definition found.";
const example = data.Result?.Training_English_Word  || "No example found.";

document.getElementById("greWordDefinition").value = definition;
document.getElementById("greWordExample").value = example;
})
.catch(error => {
console.error("Fetch error: ", error);
document.getElementById("greWordDefinition").value = `Error: ${error.message}`;
//document.getElementById("greWordExample").value = '';
});
}




// ********* Function to Submit user name or email and validate the duplicates *********
function submitUser(){
const userName = document.getElementById("userNameInput").value.trim();

if(userName === ""){
alert("Please enter a valid user name or email");
return;
}

fetch('/checkUserNameOrEmail',{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json', // Ensure it sets response type
        },
        body: JSON.stringify({
        userName: userName,
        databaseType: "dynamodb"
        })
    })
.then(response => {
if(!response.ok){
throw new Error("Failed to find user name or email.");
}
return response.json();
})
.then(data => {
if(data.exists){
window.location.href = "greWordPage.html";
}else{
window.location.href = "registerUser.html";
}
})
.catch(error =>{
console.error("Error checking user: ", error);
alert("Something went wrong. Please try again.");
});
}



// ********* Function to Save User details on AWS Dynamo DB *********
function callSaveUserDetailsApi(){
const userName = document.getElementById("userNameInput").value.trim();
const firstName = document.getElementById("firstNameInput").value.trim();
const lastName = document.getElementById("lastNameInput").value.trim();
const email = document.getElementById("emailInput").value.trim();
const address = document.getElementById("addressInput").value.trim();

if(!userName || !firstName || !lastName || !email || !address){
alert("All fields are required. Please fill in every field.");
return;
}

fetch('/postUserDetails',{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json', // Ensure it sets response type
        },
        body: JSON.stringify({
        userName,
        firstName,
        lastName,
        email,
        address,
        databaseType: "dynamodb"
        })
    })
.then(response => {
if(!response.ok){
throw new Error("Failed to save User details.");
}
return response.json();
})
.then(data => {
console.log("Response: ", data);
alert("User details saved successfully");
})
.catch(error =>{
console.error("Error saving user details: ", error);
alert("Something went wrong. Please try again.");
});
}