import { UserContext } from '../helper/getUserContext';
import { useContext, useEffect, useState, useLayoutEffect} from 'react';
import { Navigate } from 'react-router-dom';



function Mypage() {
    const { user, setUser } = useContext(UserContext)
    const [userData, setUserData] = useState(null)
    const getUserData = async () => {
        const response = await fetch("api/api/v1/my-page", {
            method: "POST"
        })
        const data = await response.json()
        return data
    }
    useEffect(() => {
        getUserData().then(data => {
            console.log(data)
            setUserData(data)})
    }, [])
    
    return (
        <div class="container my-3">
            <div class="container">
                <h1 class="mt-4">마이페이지</h1>

                <div class="card mb-4">
                    <div class="card-header">
                        회원 정보
                    </div>
                    <div class="card-body">
                        <p><strong>이름:</strong> <span id="userName">{userData.user.username}</span></p>
                        <p><strong>이메일:</strong> <span id="userEmail">{userData.user.email}</span></p>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">
                        나의 활동
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <h3>작성한 답변</h3>
                                <ul id="userAnswers" class="list-unstyled">
                                    {userData.answers.map((answer, index) => (
                                        <li key={index}>
                                            <a href={`/question/detail/${answer.questionId}`}>{answer.content}</a>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            <div class="col-md-4">
                                <h3>작성한 질문</h3>
                                <ul id="userQuestions" class="list-unstyled">
                                    {userData.questions.map((question, index) => (
                                        <li key={index}>
                                            <a href={`/question/detail/${question.id}`}>{question.title}</a>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            <div class="col-md-4">
                                <h3>작성한 댓글</h3>
                                <ul id="userComments" class="list-unstyled">
                                    {userData.comments.map((comment, index) => (
                                        <li key={index}>
                                            {comment.questionId != null && <a href={`/question/detail/${comment.questionId}`}>{comment.content}</a>}
                                            {comment.answerId != null && <a href={`/question/detail/${comment.answerId}`}>{comment.content}</a>}
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                {userData.user.oauthId == null && <a href="/change-password" id="changePasswordButton" class="btn btn-primary mt-3">비밀번호 변경</a>}        
            </div>
        </div>
    )
}

export default Mypage