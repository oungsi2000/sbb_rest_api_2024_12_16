import { useEffect, useState } from 'react';
import Header from './components/header';
import Login from "./pages/login"
import Signup from './pages/signup';
import { getUserContext, UserContext } from './helper/getUserContext';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import SignupOAuth2 from './pages/signupOAuth2';
import List from './pages/list';
import Mypage from './pages/mypage';
import ChangePassword from './pages/changePassword';
import ResetPassword from './pages/resetPassword';
import QuestionDetail from './pages/questionDetail';
import QuestionCreate from './pages/questionCreate';
import AnswerForm from './pages/answerForm'
import Authorize from './components/authorize';




function App() {

  const [user, setUser] = useState({ name: "null", role: "ANONYMOUS" });
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
          <Route path="/signup-oauth" element={<Authorize componet={SignupOAuth2} role={"TEMPORARY_USER"} />} />
          <Route path="/my-page" element={<Authorize componet={Mypage} role={["USER", "ADMIN"]}/>} />
          <Route path="/" element={<List />} />
          <Route path="/change-password" element={<Authorize componet={ChangePassword} role={["USER", "ADMIN"]}/>} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/question">
            <Route path="detail/:id" element={<QuestionDetail />} />
            <Route path="create" element={<Authorize componet={QuestionCreate} role={["USER", "ADMIN"]} />} />
            <Route path="modify/:id" element={<Authorize componet={QuestionCreate} role={["USER", "ADMIN"]} />} />
          </Route>
          <Route path="/answer/modify/:id" element={<Authorize componet={AnswerForm} role={["USER", "ADMIN"]} />} />

        </Routes>
      </UserContext.Provider>
    </BrowserRouter>
  );
}

export default App;
