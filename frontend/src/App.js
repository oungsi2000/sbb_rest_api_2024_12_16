import logo from './logo.svg';
import './App.css';
import {useEffect, useState} from 'react';
import axios from "axios";


function App() {
  const [hello, setHello] = useState('');
  useEffect(() => {
      axios.get('http://localhost:8080/')
          .then((res) => {
              setHello(res.data);
          })
  }, []);
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        {hello}
      </header>
    </div>
  );
}

export default App;