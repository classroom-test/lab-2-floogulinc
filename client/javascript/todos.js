// gets todos from the api.
// It adds the values of the various inputs to the requested URl to filter and order the returned todos.
function getFilteredTodos() {
  console.log("Getting todos");

  var url = "/api/todos?";
  if(document.getElementById("owner").value != "") {
    url = url + "&owner=" + document.getElementById("owner").value;
  }
  if(document.getElementById("category").value != "") {
    url = url + "&category=" + document.getElementById("category").value;
  }
  if(document.getElementById("status").value != "") {
    url = url + "&status=" + document.getElementById("status").value;
  }
  if(document.getElementById("contains").value != "") {
    url = url + "&contains=" + document.getElementById("contains").value;
  }
  if(document.getElementById("orderBy").value != "") {
    url = url + "&orderBy=" + document.getElementById("orderBy").value;
  }
  if(document.getElementById("limit").value != "") {
    url = url + "&limit=" + document.getElementById("limit").value;
  }

  get(url, function(returned_json){
    document.getElementById("requestUrl").innerHTML = url;
    document.getElementById('jsonDump').innerHTML = syntaxHighlight(JSON.stringify(returned_json, null, 2));
  });
}
