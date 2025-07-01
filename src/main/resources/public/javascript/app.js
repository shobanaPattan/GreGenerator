function callGetWordDetailsApi(){
const word = document.getElementById("greWordInput").value();

fetch('/getGreWordDetailsByName',{
method: 'POST',
headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json', // Ensure it sets response type
            'databaseType': 'mysql'       // Custom header your server reads
        },
        body: JSON.stringify({ name: word})
})
.then(response => {
if(!response.ok){
throw new error("Network response was not OK.");
}
return response.json();
})
.then(data =>{
document.getElementById("greWordDefinition").value = data.definition || "No definition found.";
document.getElementById("greWordExample").value = data.example || "No example found.";
})
.catch(error => {
console.error(error);
document.getElementById("greWordDefinition").value = `Error: ${error.message}`;
document.getElementById("greWordExample").value = '';
});
}