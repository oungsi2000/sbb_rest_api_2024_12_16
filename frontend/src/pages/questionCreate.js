import { useEffect, useState } from "react";
import { useParams, useLocation } from 'react-router-dom';
import { rootUrl, restServerUrl } from "../static/urlContext";
import { event2FormData } from "../helper/event2FormData";


function CategoryModal() {
    const [err, setErr] = useState(null)

    const create = (e) => {
        e.preventDefault()
        let originalResponse;

        const formDataObj = event2FormData(e)
    

        fetch('/api/api/v1/category/create', {
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
                    window.location = "/question/create"
                }
            });
        }

    return (
        <div className="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h1 className="modal-title fs-5" id="exampleModalLabel">카테고리 추가</h1>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <form onSubmit={create} method="post">
                        {err !== null && <div className="alert alert-danger" role="alert">
                            <div>{err.message}</div>
                        </div>}
                        <div className="modal-body">
                            <div className="mb-3">
                                <label className="form-label">카테고리 이름</label>
                                <input type="text" name="title" className="form-control" />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">카테고리 설명</label>
                                <input type="text" name="info" className="form-control" />
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                            <button type="submit" className="btn btn-primary">저장</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}
function QuestionCreate() {
    const [err, setErr] = useState(null)
    const [categories, setCategories] = useState(null)
    const [previousContent, setpreviousContent] = useState(null)
    const location = useLocation();
    const isModifyPage = location.pathname.includes('modify');
    const { id } = useParams()

    const create = (e) => {
        e.preventDefault()
        let originalResponse;
        let url = "";

        const formDataObj = event2FormData(e)

        if (isModifyPage) {
            url = `/api/api/v1/question/modify/${id}`
        } else {
            url = "/api/api/v1/question/create"
        }
        fetch(url, {
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
    const getCategories = async () => {
        const response = await fetch(`${restServerUrl}/api/v1/category/list`, {
            method: "GET"
        })
        const data = await response.json()
        return data
    }
    const getPreviousContent = async () => {

        if (id === null) { return }
        const response = await fetch(`${restServerUrl}/api/v1/question/modify/${id}`, {
            method: "GET"
        })
        const data = await response.json()
        return data
    }

    function setCategory(e) {
        const element = e.target.closest('li')
        const category = document.getElementById("category")
        category.value = element.dataset.id
        const dropdown = document.getElementById("dropdownMenuButton")
        dropdown.textContent = element.dataset.title
    }

    function setNullCategory(title) {
        const dropdown = document.getElementById("dropdownMenuButton")
        dropdown.textContent = title
    }

    useEffect(() => {
        getCategories().then(data => setCategories(data))
    }, [])

    useEffect(()=>{
        getPreviousContent()
        .then(data => setpreviousContent(data))
    }, [])

    useEffect(()=>{
        if (previousContent === null) {return}
        document.getElementById("subject").value = previousContent.subject 
        document.getElementById("content").value = previousContent.content  
    })

    return (
        <div className="container">
            <h5 className="my-3 border-bottom pb-2">질문등록</h5>
            <form onSubmit={create}>
                {err !== null && <div className="alert alert-danger" role="alert">
                    <div>{err.message}</div>
                </div>}
                <div className="mb-3">
                    <label for="subject" className="form-label">제목</label>
                    <input type="text" name="subject" id="subject" className="form-control" />
                </div>
                <div className="dropdown">
                    <input type="hidden" name="categoryId" id="category" />
                    <button className="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
                        카테고리를 선택해주세요
                    </button>
                    <ul className="dropdown-menu" aria-labelledby="dropdownMenuButton">
                        <li onClick={()=>{setNullCategory('카테고리 없음')}}>
                            <button type="button" className="dropdown-item" href="#">카테고리 없음</button>
                        </li>

                        {categories !== null && categories.categories.map(
                            (category, index) => (
                                <li key={index} data-title={category.title} data-id={category.id} onClick={(e)=>{setCategory(e)}}>
                                    <button type="button" className="dropdown-item">{category.title}</button>
                                </li>
                            )
                        )}


                        <li>
                            <button type="button" className="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
                                카테고리 추가
                            </button>
                        </li>
                    </ul>
                </div>
                <div className="mb-3 mt-3">
                    <label for="content" className="form-label">내용</label>
                    <textarea name="content" id="content" className="form-control" rows="10"></textarea>
                </div>
                <input type="submit" value="저장하기" className="btn btn-primary my-2" />
            </form>
            <CategoryModal/>

        </div>

    )
}

export default QuestionCreate