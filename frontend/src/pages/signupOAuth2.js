import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../helper/getUserContext';


function SignupOAuth2() {
    const { user, setUser } = useContext(UserContext)
    const [err, setErr] = useState(null)
    const signupOAuth2 = (e) => {
        e.preventDefault()
        const form = e.target;
        let originalResponse;

        const formDataObj = new FormData(form);
        const data = {};
        for (let [key, value] of formDataObj.entries()) {
            data[key] = value;
        }

        fetch('/api/api/v1/signup/oauth', {
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
        <>
            {user.role === "TEMPORARY_USER" && <div class="container my-3">
                <div class="my-3 border-bottom">
                    <div>
                        <h4>회원가입</h4>
                    </div>
                </div>
                <form method="post" onSubmit={signupOAuth2}>
                    {err !== null && 
                    <div>
                        <div class="alert alert-danger">{err.message}</div>
                    </div>}
                    <div class="mb-3">
                        <label for="username" class="form-label">사용자ID</label>
                        <input type="text" name="username" class="form-control" />
                    </div>

                    <button type="submit" class="btn btn-primary">회원가입</button>
                </form>
            </div>}
        </>
    )
}

export default SignupOAuth2