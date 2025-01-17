import { useEffect, useState, useContext, useRef } from "react";
import { useParams } from 'react-router-dom';
import { UserContext } from '../helper/getUserContext';
import { marked } from 'marked';
import { format } from 'date-fns';
import _, { rest } from 'lodash';
import { rootUrl, restServerUrl } from "../static/urlContext";
import {event2FormData} from "../helper/event2FormData"


function QuestionCommentForm(props) {
    const question = props.question
    const [err, setErr] = useState(null)

    const createQuestionComment = (e) => {
        e.preventDefault()
        let originalResponse;
        const formDataObj = event2FormData(e)

        fetch(`/api/api/v1/question/create/comment`, {
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
                    window.location = `${rootUrl}/question/detail/${question.id}`

                }
            });
    }
    return (
        <form onSubmit={createQuestionComment} method="post">
            <div className="d-flex justify-content-lg-between align-items-center">
                <h4>댓글 작성</h4>
                <button type="submit" className="btn btn-primary">등록</button>
            </div>
            <div className="mb-3 mt-2">
                <textarea name="content" className="form-control" rows="3"></textarea>
                <input name="id" hidden value={question.id} type="text" />
            </div>
        </form>
    )
}

function QuestionCommentList(props) {
    const questionComments = props.questionComments
    return (
        <ul className="list-group col-6 mt-3">
            {questionComments.map(
                (comment, index) => (
                    <li key={index} className="list-group-item">
                        <div className="d-flex w-100 justify-content-between">
                            <h5 className="mb-1">{comment.author.username}</h5>
                        </div>
                        <p className="mb-1">{comment.content}</p>
                    </li>
                )
            )}

        </ul>
    )
}

function Question(props) {
    const question = props.questionDetail.question
    const html = marked.parse(question.content);
    const { user, setUser } = useContext(UserContext)

    const deleteQuestion = async () => {
        const response = await fetch(`${restServerUrl}/api/v1/question/delete/${question.id}`, {
            method: "GET"
        })
        const data = await response.json()
        return data
    }

    const voteQuestion = async () => {
        const response = await fetch(`${restServerUrl}/api/v1/question/vote/${question.id}`, {
            method: "POST"
        })
        const data = await response.json()
        return data
    }

    useEffect(() => {
        const recommend_elements = document.getElementsByClassName("recommend");
        Array.from(recommend_elements).forEach(function (element) {
            if (element.hasOwnProperty('clickListenerAdded')) { return }
            element.addEventListener('click', function () {
                if (window.confirm("정말로 추천하시겠습니까?")) {
                    voteQuestion().then(data => {
                        alert(data.message)
                        window.location = `${rootUrl}/question/detail/${question.id}`
                    })
                };
            });
            element.clickListenerAdded = true;
        });
    }, [])

    return (
        <>
            <h2 className="py-2">{question.title}</h2>
            {question.category != null && <h6 className="border-bottom py-1 mb-1">카테고리 : {question.category.title}</h6>}
            <div className="card my-3">
                <div className="card-body">
                    <div className="card-text" dangerouslySetInnerHTML={{ __html: html }}></div>
                    <div className="d-flex justify-content-end">
                        {question.modifyDate != null && <div className="badge bg-light text-dark p-2 text-start mx-3">
                            <div className="mb-2">modified at</div>
                            <div>{format(new Date(question.modifyDate), 'yyyy-MM-dd HH:mm')}</div>
                        </div>}
                        <div className="badge bg-light text-dark p-2 text-start">
                            <div className="mb-2">
                                {question.author != null && <span>{question.author.username}</span>}
                            </div>
                            <div>{format(new Date(question.createDate), 'yyyy-MM-dd HH:mm')}</div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="my-3">
                <button className="recommend btn btn-sm btn-outline-secondary">추천
                    <span className="badge rounded-pill bg-success">{question.voter}</span>
                </button>
                {(user.role === "USER" &&
                    question.author != null &&
                    (user.name === question.author.username || user.name === question.author.email))
                    && <a href={`/question/modify/${question.id}`} className="btn btn-sm btn-outline-secondary">수정</a>}

                {(user.role === "USER" &&
                    question.author != null &&
                    (user.name === question.author.username || user.name === question.author.email)) &&
                    <button onClick={() => {
                        deleteQuestion()
                            .then(
                                data => {
                                    alert(data.message)
                                    if (data.status_code === 200) {
                                        window.location = "/"
                                    }
                                }
                            )
                    }}
                        className="delete btn btn-sm btn-outline-secondary">삭제</button>}

                {user.role === "USER" &&
                    <div className="comment-form mt-3 col-6">
                        <QuestionCommentForm question={question} />
                    </div>}
                <QuestionCommentList questionComments={props.questionDetail.questionComments} />
            </div>
        </>
    )
}

function AnswerPaging(props) {
    const answers = props.answers
    const firstButtonclassName = `page-item ${answers.first ? 'disabled' : ''}`;
    const lastButtonclassName = `page-item ${answers.last ? 'disabled' : ''}`;
    const firstDataPage = answers.number - 1
    const lastDataPage = answers.number + 1
    const { id } = useParams()

    useEffect(() => {
        const page_elements = document.getElementsByClassName("page-link");
        Array.from(page_elements).forEach(function (element) {
            element.addEventListener('click', function () {
                document.getElementById('page').value = this.dataset.page;
                document.getElementById('searchForm').submit();
            });
        });
    }, [])

    return (
        <div className="d-flex justify-content-between align-items-center">
            <div>
                <a href={`${rootUrl}/question/detail/${id}/?sortBy=present`} className="sort btn btn-sm btn-outline-secondary">
                    최신 순
                </a>
                <a href={`${rootUrl}/question/detail/${id}/?sortBy=mostVoted`} className="sort btn btn-sm btn-outline-secondary">
                    추천 순
                </a>
            </div>
            {!(answers.empty) && <div>
                <ul className="pagination justify-content-center">
                    <li className={firstButtonclassName} key={-1}>
                        <button className="page-link" data-page={firstDataPage}>
                            <span>이전</span>
                        </button>
                    </li>

                    {_.range(0, answers.totalPages - 1).map(
                        page => (
                            (page >= answers.number - 5 && page <= answers.number + 5) &&
                            <li className={`page-item ${page === answers.number ? 'active' : ''}`}
                                key={page}>
                                <button className="page-link" data-page={page}>{page}</button>
                            </li>

                        ))}

                    <li className={lastButtonclassName} key={answers.totalPages + 1}>
                        <button className="page-link" data-page={lastDataPage}>
                            <span>다음</span>
                        </button>
                    </li>
                </ul>
                <form method="get" id="searchForm">
                    <input type="hidden" id="page" name="index" value={answers.number} />
                </form>
            </div>}
        </div>
    )
}

function AnswerCreateForm(props) {
    const question = props.question
    const { user, setUser } = useContext(UserContext)
    const [err, setErr] = useState(null)

    const createAnswer = (e) => {
        e.preventDefault()
        let originalResponse;
        const formDataObj = event2FormData(e)

        fetch(`/api/api/v1/answer/create/${question.id}`, {
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
                    window.location = `${rootUrl}/question/detail/${question.id}`
                }
            });
    }

    return (
        <form onSubmit={createAnswer} method="post" className="my-3">
            {err !== null && <div className="alert alert-danger" role="alert">
                <div>{err.message}</div>
            </div>}
            {user.role === "USER" && <textarea name="content" className="form-control" rows="10"></textarea>}
            {user.role === "USER" && <input type="submit" value="답변등록" className="btn btn-primary my-2" />}
        </form>
    )
};



function Answercomment(props) {
    const answer = props.answer
    const loop = props.loop
    const answerComments = props.answerComments !== undefined ? props.answerComments : []
    const { user, setUser } = useContext(UserContext)
    const [err, setErr] = useState(null)

    const createAnswerComment = (e) => {
        e.preventDefault()
        let originalResponse;

        const formDataObj = event2FormData(e)

        fetch(`/api/api/v1/answer/create/comment`, {
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
        <div className={`comment-form mt-3 col-6 d-none answer-comment${loop}`} >
            {user.role === "USER" && <form onSubmit={createAnswerComment}>
                <input name="id" hidden value={answer.id} type="text" />
                <div className="d-flex justify-content-lg-between align-items-center">
                    <h4>댓글 작성</h4>
                    <button type="submit" className="btn btn-primary">등록</button>
                </div>
                <div className="mb-3 mt-2">
                    <textarea name="content" className="form-control" rows="3"></textarea>
                </div>
            </form>}

            <ul className="list-group col-6 mt-3">
                {answerComments.map(
                    (comment, loop) => (
                        <li className="list-group-item" key={loop}>
                            <div className="d-flex w-100 justify-content-between">
                                <h5 className="mb-1" >{comment.author.username}</h5>
                            </div>
                            <p className="mb-1">{comment.content}</p>
                        </li>
                    )
                )}
            </ul>
        </div>
    )
}

function AnswerTimeInfo(props) {
    const answer = props.answer
    return (
        <div className="d-flex justify-content-end">
            {answer.modifyDate != null &&
                <div className="badge bg-light text-dark p-2 text-start mx-3">
                    <div className="mb-2">modified at</div>
                    <div>{format(new Date(answer.modifyDate), 'yyyy-MM-dd HH:mm')}</div>
                </div>}
            <div className="badge bg-light text-dark p-2 text-start">
                <div className="mb-2">
                    {answer.author != null && <span>{answer.author.username}</span>}
                </div>
                <div>{format(new Date(answer.createDate), 'yyyy-MM-dd HH:mm')}</div>
            </div>
        </div>
    )
}

function AnswerControllButtons(props) {
    const answer = props.answer
    const question = props.question
    const loop = props.loop
    const { user, setUser } = useContext(UserContext)

    const deleteAnswer = async (answer) => {
        const response = await fetch(`${restServerUrl}/api/v1/answer/delete/${answer.id}`, {
            method: "GET"
        })
        const data = await response.json()
        return data
    }

    const voteAnswer = async (answer) => {
        const response = await fetch(`${restServerUrl}/api/v1/answer/vote/${answer.id}`, {
            method: "POST"
        })
        const data = await response.json()
        return data
    }
    const showComment = (index) => {
        const answerComment = document.getElementsByClassName("answer-comment" + index);

        Array.from(answerComment).forEach((element) => {
            if (element.className.includes('d-none')) {
                element.className = "comment-form mt-3 col-6 answer-comment" + index
            } else {
                element.className = "comment-form mt-3 col-6 d-none answer-comment" + index
            }
        })
    }
    return (
        <>
            <button className="recommend btn btn-sm btn-outline-secondary"
                onClick={() => {
                    if (!window.confirm("정말로 추천하시겠습니까?")) { return }
                    voteAnswer(answer).then(data => {
                        alert(data.message)
                        window.location = `${rootUrl}/question/detail/${question.id}`
                    })
                }}>
                추천
                <span className="badge rounded-pill bg-success">{answer.voter}</span>
            </button>
            {(user.role === "USER" &&
                answer.author != null &&
                (user.name === answer.author.username || user.name === answer.author.email))
                && <a href={`/answer/modify/${answer.id}`} className="btn btn-sm btn-outline-secondary">수정</a>}

            {(user.role === "USER" &&
                answer.author != null &&
                (user.name === answer.author.username || user.name === answer.author.email)) && <button
                    onClick={
                        () => {
                            if (!window.confirm("정말로 삭제하시겠습니까?")) { return }
                            deleteAnswer(answer)
                                .then(resposne => {
                                    alert(resposne.message)
                                    window.location = `${rootUrl}/question/detail/${question.id}`
                                })
                        }
                    }
                    className="delete btn btn-sm btn-outline-secondary">삭제</button>}
            <button className="btn btn-sm btn-outline-secondary" onClick={() => showComment(loop)}>댓글</button>
        </>
    )
}

function QuestionDetail() {
    const { id } = useParams()
    const [questionDetail, setQuestionDetail] = useState(null)
    const { user, setUser } = useContext(UserContext)
    const currentURL = new URL(window.location.href);

    const getQuestionDetail = async () => {
        const index = currentURL.searchParams.get('index')
        const sortBy = currentURL.searchParams.get('sortby')
        let url = `${restServerUrl}/api/v1/question/detail/` + id

        if (index !== null) {
            url += '?index=' + index
        }

        if (sortBy !== null) {
            url += '?sortBy=' + sortBy
        }

        const response = await fetch(url, {
            method: "POST"
        })
        
        if (response.status === 404) {return null}
        const data = await response.json()
        return data
    }
    useEffect(() => {
        getQuestionDetail().then(data => setQuestionDetail(data))
    }, [])


    return (
        <>
            {questionDetail !== null && <div className="container my-3">
                <Question questionDetail={questionDetail} />

                <h5 className="border-bottom my-3 py-2">{questionDetail.answers.totalElements}개의 답변이 있습니다</h5>
                <AnswerPaging answers={questionDetail.answers} />

                {questionDetail.answers.content.map(

                    (answer, loop) => (<div className="card my-3">
                        <a id={`answer_${answer.id}`}></a>
                        <div className="card-body">
                            <div className="card-text" dangerouslySetInnerHTML={{ __html: marked.parse(answer.content) }}></div>

                            <AnswerTimeInfo answer={answer} />
                            <div className="my-3">
                                <AnswerControllButtons answer={answer} loop={loop} question={questionDetail.question}/>
                                <Answercomment answer={answer} loop={loop} answerComments={questionDetail.answerComments[answer.id]} />
                            </div>
                        </div>
                    </div>)
                )
                }
                <AnswerCreateForm question={questionDetail.question} />
            </div>}
        </>
    )
}

export default QuestionDetail