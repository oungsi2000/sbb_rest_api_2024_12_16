import { useEffect } from "react"

function ChangePassword() {

    const containerStyle = {
        maxWidth: "400px"
    }
    const errorMessageStyle = {
        color: "red",
        fontSize: "0.875rem", /* 부트스트랩 기본 폰트 크기에 맞춤 */
        marginTop: "0.25rem"
    }

    useEffect(()=>{
        const form = document.getElementById('passwordChangeForm');
        const currentPasswordInput = document.getElementById('currentPassword');
        const newPasswordInput = document.getElementById('newPassword');
        const confirmPasswordInput = document.getElementById('newPasswordConfirm');
        const currentPasswordError = document.getElementById('currentPasswordError');
        const confirmPasswordError = document.getElementById('confirmPasswordError')
        const newPasswordError = document.getElementById('newPasswordError');
        
    
        form.addEventListener('submit', function(event) {
            event.preventDefault();
            resetErrorMessages();
    
            let isValid = true;
    
            if (newPasswordInput.value !== confirmPasswordInput.value) {
                confirmPasswordError.textContent = "새 비밀번호와 확인이 일치하지 않습니다.";
                isValid = false;
            }
    
            if (newPasswordInput.value.length < 8) {
                newPasswordError.textContent = "새 비밀번호는 최소 8자 이상이어야 합니다.";
                isValid = false;
            }
            if (!/[a-zA-Z]/.test(newPasswordInput.value) || !/[0-9]/.test(newPasswordInput.value) || !/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(newPasswordInput.value)) {
                newPasswordError.textContent = "새 비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.";
                isValid = false;
            }
    
            if (isValid) {
                fetch('api/api/v1/change-password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams(new FormData(form)).toString()
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(error => { throw error; });
                        }
                        return response;
                    })
                    .then(data => {
                        alert("비밀번호가 성공적으로 변경되었습니다.");
                        window.location.href = '/';
                    })
                    .catch(error => {
                        console.error("오류 발생:", error);
                        if (error.message) {
                            alert(error.message);
                        } else {
                            alert("비밀번호 변경 중 오류가 발생했습니다.");
                        }
                    });
            }
        });
        function resetErrorMessages() {
            currentPasswordError.textContent = "";
            newPasswordError.textContent = "";
            confirmPasswordError.textContent = "";
        }
    }, [])
    return (

        <div className="container" style={containerStyle}>
            <div className="card">
                <div className="card-body">
                    <h1 className="card-title">비밀번호 변경</h1>
                    <form id="passwordChangeForm">
                        <div className="mb-3">
                            <label className="form-label">현재 비밀번호</label>
                            <input type="password" className="form-control" id = "password" name="password" required />
                            <div id="currentPasswordError" className="error-message" style={errorMessageStyle}></div>
                        </div>
                        <div className="mb-3">
                            <label className="form-label">새 비밀번호</label>
                            <input type="password" className="form-control" id = "newPassword" name="newPassword" required />
                            <div id="newPasswordError" className="error-message" style={errorMessageStyle}></div>
                        </div>
                        <div className="mb-3">
                            <label className="form-label">새 비밀번호 확인</label>
                            <input type="password" className="form-control" id = "newPasswordConfirm" name="newPasswordConfirm" required />
                            <div id="confirmPasswordError" className="error-message" style={errorMessageStyle}></div>
                        </div>
                        <button type="submit" className="btn btn-primary">비밀번호 변경</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default ChangePassword