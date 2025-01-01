const urlBase = 'http://localhost:8080/';
let pathHistory = [];

async function fetchDirectory (path) {

  try {
    console.log('Solicitando archivos de: ' + urlBase + path);
    const response = await fetch(urlBase + path);
    
    if(response.ok) { 
      const data = await response.json();
      console.log(data);
      renderFileList(data);

      if(pathHistory[pathHistory.length - 1] !== path) {
        pathHistory.push(path);
      }

      return data;
    }
  } catch (error) {
    console.error(error);
  }
}

function renderFileList(fileList) {
  const fileListElement = document.getElementById('file-list');

  fileListElement.innerHTML = '';
  // Si el directorio está vacío
  if(fileList.length === 0) {
    const message = document.createElement('p');
    message.className = 'fw-bold';
    message.textContent = 'No hay archivos disponibles';
    fileListElement.append(message);
  }

  // Si el directorio contiene archivos.
  else {
    fileList.forEach(file => {
      const listItem = document.createElement('li');
      const icon = document.createElement('i');
      const link = document.createElement('a');
  
      icon.className = file.type === 'directory' ? 'fas fa-folder file-icon' : 'fas fa-file file-icon';
      icon.style.color = file.type === 'directory' ? '#f2eb0f' : '#1ca6f5';
      link.textContent = file.name;
  
      if(file.type == 'directory') link.onclick = () => fetchDirectory(`api/files` + file.filepath);
      else link.href = file.filepath;
  
      listItem.appendChild(icon);
      listItem.appendChild(link);
      fileListElement.appendChild(listItem);
    })
  }
}

const showPaths = () => {
  if(pathHistory.length > 1) {
    pathHistory.forEach(path => {
      console.log(path);
    });
  }
}

const goBack = () => {
  if(pathHistory.length > 1) {
    pathHistory.pop();
    const previous = pathHistory[pathHistory.length - 1];
    fetchDirectory(previous);
  }

  else {
    console.log('No hay ruta anterior');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  fetchDirectory('api/files/');

  const backButton = document.getElementById('back-button');
  if(backButton) {
    backButton.addEventListener('click', goBack);
  }
});