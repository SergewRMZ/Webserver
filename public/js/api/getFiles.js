const urlBase = 'http://localhost:8080/';

async function fetchDirectory (path) {
  try {
    console.log('Solicitando archivos de: ' + urlBase + path);
    const response = await fetch(urlBase + path);
    if(response.ok) { 
      const data = await response.json();
      console.log(data);
      renderFileList(data);
      return data;
    }
  } catch (error) {
    console.error(error);
  }
}

function renderFileList(fileList) {
  const fileListElement = document.getElementById('file-list');

  fileListElement.innerHTML = '';

  fileList.forEach(file => {
    const listItem = document.createElement('li');
    const icon = document.createElement('i');
    const link = document.createElement('a');

    icon.className = file.type === 'directory' ? 'fas fa-folder file-icon' : 'fas fa-file file-icon';
    icon.style.color = file.type === 'directory' ? '#f2eb0f' : '#1ca6f5';
    link.textContent = file.name;

    if(file.type == 'directory') {
      link.onclick = () => fetchDirectory(`api/files` + file.filepath);
    }

    else link.href = file.filepath;
    listItem.appendChild(icon);
    listItem.appendChild(link);
    fileListElement.appendChild(listItem);
  })
}

document.addEventListener('DOMContentLoaded', () => {
  fetchDirectory('api/files/');
});