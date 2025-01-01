import { useEffect, useState } from "react";
import { useParams, useLocation } from 'react-router-dom';

function AnswerForm() {
    const [err, setErr] = useState(null)
    const { id } = useParams()

    const modify = (e) => {
        e.preventDefault()
        const form = e.target;
        let originalResponse;

        const formDataObj = new FormData(form);
        const data = {};
        for (let [key, value] of formDataObj.entries()) {
            data[key] = value;
        }

        fetch('/api/api/v1/answer/modify/'+id, {
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
                    window.location = result.redirectUrl
                }
            });
        }
    
    return (
        <div class="container">
            <h5 class="my-3 border-bottom pb-2">답변 수정</h5>
            <form onSubmit={modify}>
               {err !== null && <div class="alert alert-danger" role="alert">
                    <div>{err.message}</div>
                </div>}
                <div class="mb-3">
                    <label for="content" class="form-label">내용</label>
                    <textarea name="content" class="form-control" rows="10"></textarea>
                </div>
                <input type="submit" value="저장하기" class="btn btn-primary my-2" />
            </form>
        </div>
    )
}

export default AnswerForm