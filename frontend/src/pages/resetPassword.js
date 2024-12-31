import { useEffect } from "react"

function ResetPassword() {
    useEffect(()=>{
        const form = document.getElementById('findPasswordForm');
        const messageArea = document.getElementById('message-area');
    
        form.addEventListener('submit', (event) => {
            event.preventDefault(); // 기본 제출 동작 방지
           
            const email = document.getElementById('email').value;
            const params = new URLSearchParams();
            params.append('email', email);
    
            fetch('api/api/v1/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params,
    
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {throw new Error(text)});
                    }
                    return response.text();
                })
                .then(data => {
                    // 성공 메시지 표시
                    messageArea.innerHTML = `<div class="alert alert-success" role="alert">${data}</div>`;
                    form.reset(); // 폼 초기화
                })
                .catch(error => {
                    // 오류 메시지 표시
                    console.error('Error:', error);
                    messageArea.innerHTML = `<div class="alert alert-danger" role="alert">${error}</div>`;
                });
        });
    }, [])
    return (
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            임시 비밀번호 발송
                        </div>
                        <div class="card-body">
                            <div id="message-area"></div> <form id="findPasswordForm">
                                <div class="mb-3">
                                    <label for="email" class="form-label">이메일 주소</label>
                                    <input type="email" class="form-control" id="email" name="email" placeholder="이메일 주소를 입력하세요" required/>
                                </div>
                                <button type="submit" class="btn btn-primary">임시 비밀번호 발송</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ResetPassword