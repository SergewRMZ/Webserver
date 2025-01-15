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
      listItem.className = 'd-flex justify-content-between align-items-center border p-2 mb-2';
      listItem.style.width = '40%';

      // Contenedor para el ícono y el nombre
      const fileInfoContainer = document.createElement('div');
      fileInfoContainer.className = 'd-flex align-items-center';

      // Contenedor botones
      const Buttons = document.createElement('div');
      
      const icon = document.createElement('i');
      const link = document.createElement('a');
      const renameButton = document.createElement('button');
      const deleteButton = document.createElement('button');
  
      icon.className = file.type === 'directory' ? 'fas fa-folder file-icon' : 'fas fa-file file-icon';
      icon.style.color = file.type === 'directory' ? '#f2eb0f' : '#1ca6f5';
      link.textContent = file.name;

      // Botón de cambiar nombre
      renameButton.className = 'btn btn-info btn-sm m-1';
      renameButton.textContent = 'Renombrar';
      renameButton.onclick = async () => {
        const { value: newName } = await Swal.fire({
          title: `Renombrar "${file.name}"`,
          input: 'text',
          inputLabel: 'Nuevo nombre del archivo:',
          inputValue: file.name,
          showCancelButton: true,
          confirmButtonText: 'Renombrar',
          cancelButtonText: 'Cancelar',
          inputValidator: value => {
            if (!value) {
              return '¡El nombre no puede estar vacío!';
            }
          },
        });

        if (newName) {
          try {
            const response = await fetch(`http://localhost:8080/api/files${file.filepath}`, {
              method: 'PUT',
              headers: {
                'Content-Type': 'application/json',
              },
              body: JSON.stringify({ filename: newName }),
            });

            if (response.ok) {
              Swal.fire('Renombrado', `El archivo ha sido renombrado a "${newName}"`, 'success');
              fetchDirectory('api/files/');
            } else {
              Swal.fire('Error', 'No se pudo renombrar el archivo', 'error');
            }
          } catch (error) {
            console.error('Error:', error);
            Swal.fire('Error', 'Hubo un problema con la solicitud', 'error');
          }
        }
      };
      // Botón de eliminar
      deleteButton.className = 'btn btn-danger btn-sm m-1';
      deleteButton.textContent = 'Eliminar';
      deleteButton.onclick = async () => {
        const result = await Swal.fire({
          title: `¿Eliminar "${file.name}"?`,
          text: 'Esta acción no se puede deshacer',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonText: 'Eliminar',
          cancelButtonText: 'Cancelar',
        });

        if (result.isConfirmed) {
          try {
            const response = await fetch(`http://localhost:8080/api/files${file.filepath}`, {
              method: 'DELETE',
            });

            if (response.ok) {
              Swal.fire('Eliminado', `El archivo "${file.name}" ha sido eliminado`, 'success');
              fetchDirectory('api/files/');
            } else {
              Swal.fire('Error', 'No se pudo eliminar el archivo', 'error');
            }
          } catch (error) {
            console.error('Error:', error);
            Swal.fire('Error', 'Hubo un problema con la solicitud', 'error');
          }
        }
      };
  
      if(file.type == 'directory') link.onclick = () => fetchDirectory(`api/files` + file.filepath);
      else link.href = file.filepath;
  
      fileInfoContainer.appendChild(icon);
      fileInfoContainer.appendChild(link);
      Buttons.appendChild(renameButton);
      Buttons.appendChild(deleteButton);
      listItem.appendChild(fileInfoContainer); 
      listItem.appendChild(Buttons); 
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