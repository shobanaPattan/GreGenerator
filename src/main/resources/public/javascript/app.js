function callGetWordDetailsApi(){
const word = document.getElementById("greWordInput").value();

fetch('/getGreWordDetailsByName{encodeURIComponent(word)}&databaseType=mysql')
.then(response => {
if(!response.ok){
throw new error("Network response was not OK.");
}
return response.json();
})
.then(data =>{
document.getElementById("greWordDefinition").textContent = JSON.stringify(data, null, 2);
})
.catch(error => {
document.getElementById("greWordDefinition").textContent = `Error: ${error.message}`;
});
}