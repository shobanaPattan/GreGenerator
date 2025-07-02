//Fetch GRE word details by name (on button click)
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



//On page load, fill the existing input with least viewed GRE word and details
function loadLeastViewedWord(){
fetch('/getGreWordDetailsByViewCount',
{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json', // Ensure it sets response type
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

const firstItem = data.Result?.[0]; //Access the first item in the array

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

//Run on Page Load
document.addEventListener("DOMContentLoaded",() =>{
loadLeastViewedWord();
});