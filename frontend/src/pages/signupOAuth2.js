import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../helper/getUserContext';
import { event2FormData } from '../helper/event2FormData';


function SignupOAuth2() {
    const { user, setUser } = useContext(UserContext)
    const [err, setErr] = useState(null)
    const signupOAuth2 = (e) => {
        e.preventDefault()
        let originalResponse;

        const formDataObj = event2FormData(e)
        
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
            {user.role === "TEMPORARY_USER" && <div className="container my-3">
                <div className="my-3 border-bottom">
                    <div>
                        <h4>회원가입</h4>
                    </div>
                </div>
                <form method="post" onSubmit={signupOAuth2}>
                    {err !== null && 
                    <div>
                        <div className="alert alert-danger">{err.message}</div>
                    </div>}
                    <div className="mb-3">
                        <label for="username" className="form-label">사용자ID</label>
                        <input type="text" name="username" className="form-control" />
                    </div>

                    <button type="submit" className="btn btn-primary">회원가입</button>
                </form>
            </div>}
        </>
    )
}

export default SignupOAuth2