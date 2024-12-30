import { useState } from "react";

function Login() {
    const [err, setErr] = useState(null)
    const login = (e)=> {
        e.preventDefault()
        const form = e.target;
        let originalResponse;

        const formDataObj = new FormData(form);
        const data = {};
        for (let [key, value] of formDataObj.entries()) {
          data[key] = value;
        }

        fetch('/api/api/v1/login', {
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
            <form onSubmit={login}>
                {err !== null && <div>
                    <div className="alert alert-danger">
                        사용자ID 또는 비밀번호를 확인해 주세요.
                    </div>
                </div>}
                <div className="mb-3">
                    <label for="username" className="form-label">사용자ID</label>
                    <input type="text" name="username" id="username" className="form-control"/>
                </div>
                <div className="mb-3">
                    <label for="password" className="form-label">비밀번호</label>
                    <input type="password" name="password" id="password" className="form-control"/>
                </div>
                <button type="submit" className="btn btn-primary">로그인</button>
            </form>
            <a className="btn btn-link text-decoration-underline mt-3" href="/">비밀번호 찾기</a>
        </div>
    )
}

export default Login