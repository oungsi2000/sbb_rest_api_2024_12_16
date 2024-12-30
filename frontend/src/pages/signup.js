import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../helper/getUserContext';

function Signup() {
    const [err, setErr] = useState(null)
    const signup = (e)=>{
        e.preventDefault()
        const form = e.target;
        let originalResponse;

        const formDataObj = new FormData(form);
        const data = {};
        for (let [key, value] of formDataObj.entries()) {
            data[key] = value;
        }
        fetch('/api/api/v1/signup', {
            method: 'POST',
            body: formDataObj // FormData 객체 직접 전송
        })
            .then(response => {
                originalResponse = response;
                return response.json()
            })
            .then(result => {
                if (originalResponse.status !== 200) {
                    setErr(result)
                } else {
                    setErr(null)
                    window.location = "/"
                }
            });
    };
    
    return (
        <div className="container my-3">
            <div className="my-3 border-bottom">
                <div>
                    <h4>회원가입</h4>
                </div>
            </div>
            <form onSubmit={signup} method="post">
                {err !== null && <div>
                    <div className="alert alert-danger">{err.message}</div>
                </div>}
                <div className="mb-3">
                    <label for="username" className="form-label">사용자ID</label>
                    <input type="text" name="username" className="form-control"/>
                </div>
                <div className="mb-3">
                    <label for="password1" className="form-label">비밀번호</label>
                    <input type="password" name="password1" className="form-control"/>
                </div>
                <div className="mb-3">
                    <label for="password2" className="form-label">비밀번호 확인</label>
                    <input type="password" name="password2" className="form-control"/>
                </div>
                <div className="mb-3">
                    <label for="email" className="form-label">이메일</label>
                    <input type="email" name="email" className="form-control"/>
                </div>
                <button type="submit" className="btn btn-primary">회원가입</button>
            </form>
        </div>
    )
}

export default Signup