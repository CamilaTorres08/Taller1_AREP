/*
*Funcion para cargar las tareas
*/
function loadTask(array) {
    console.log(array);
    let taskhtml = '';
    for(let i=0; i<array.length;i++){
        const task = array[i];
        taskhtml+= `
        <div class="task">
            <h2>${task.name}</h2>   
            <p>${task.description}</p>
        </div>`
    }
    document.getElementById("task-container").innerHTML = taskhtml;
}

/*
*Funcion para añadir una tarea
*/
function addTask(){
    const taskName = document.getElementById("taskTitle").value;
    const description = document.getElementById("taskDescription").value;
    if (!taskName || !description) {
        alert('Please fill all the fields');
        return;
    }
    if(taskName.length > 20){
        alert('The title is too long');
        return;
    }
    if (description.length > 30) {
        alert('The description is too long');
        return;
    }
    console.log("entrooo");
    fetch("http://localhost:35000/app/saveTask",
        {
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            method: "POST",
            body: JSON.stringify({
                name: taskName,
                description: description,
            })
        })
        .then(res => res.json())
        .then(data => console.log(data));
    //loadTask();
}

function searchTasks(){
    const searchValue = document.getElementById("taskSearch").value;
    if(!searchValue){
        document.getElementById("filter").innerText = "All";
    }else{
        document.getElementById("filter").innerText = searchValue;
    }
    const value = document.getElementById("filter").innerText;
    console.log("VALUE: ",value);
    fetch("http://localhost:35000/app/tasks?name="+value,
        {
            method: "GET"
        })
        .then(res => res.json())
        .then(data => loadTask(data));
}


window.addTask = addTask;
window.searchTasks = searchTasks;
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("filter").innerText = "All";
    loadTask();
});
