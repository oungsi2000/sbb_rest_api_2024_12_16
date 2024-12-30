import { useEffect, useState } from 'react';
// import axios from "axios";
import Header from './components/header';
import Login from "./pages/login"
import Signup from './pages/signup';
import { getUserContext, UserContext } from './helper/getUserContext';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import SignupOAuth2 from './pages/signupOAuth2';




function App() {
  
  const [user, setUser] = useState({name:"null", role:"ANONYMOUS"});
  useEffect(() => {
    getUserContext().then((result => {
      setUser(result)
    }))
  }, [])

  // const [hello, setHello] = useState('');
  // useEffect(() => {
  //     axios.get('http://localhost:8080/')
  //         .then((res) => {
  //             setHello(res.data);
  //         })
  // }, []);
  return (
    <BrowserRouter>
      <UserContext.Provider value={{ user, setUser }}>
        <Header />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/signup-oauth" element={<SignupOAuth2 />}/>
        </Routes>
      </UserContext.Provider>
    </BrowserRouter>
  );
}

export default App;
