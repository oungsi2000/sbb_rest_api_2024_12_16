import { useState, useEffect, useRef } from "react"
import _ from 'lodash';



function SearchBar(props) {
    const list = props.list

    useEffect(()=>{
        const btn_search = document.getElementById("btn_search");
            btn_search.addEventListener('click', function() {
                document.getElementById('kw').value = document.getElementById('search_kw').value;
                document.getElementById('page').value = 0;  // 검색버튼을 클릭할 경우 0페이지부터 조회한다.
                document.getElementById('searchForm').submit();
            });
    }, [])
    
    return (
        <>
        <div className="col-6">
            <div className="input-group">
                <input type="text" id="search_kw" className="form-control"/>
                <button className="btn btn-outline-secondary" type="button" id="btn_search">찾기</button>
            </div>
        </div>
        <form action="/" method="get" id="searchForm">
            <input type="hidden" id="kw" name="kw" value=""/>
            <input type="hidden" id="page" name="page" value={list.number}/>
        </form>
        </>
    )
}


function Paging(props) {
    const list = props.list

    const firstButtonClassName = `page-item ${list.first ? 'disabled' : ''}`;
    const lastButtonClassName = `page-item ${list.last ? 'disabled' : ''}`;
    const firstDataPage = list.number - 1
    const lastDataPage = list.number + 1

    useEffect(()=>{
        const page_elements = document.getElementsByClassName("page-link");

        Array.from(page_elements).forEach(function(element) {
            element.addEventListener('click', function() {
                document.getElementById('page').value = this.dataset.page;
                document.getElementById('searchForm').submit();
            });
        });
    }, [list.number, list.totalPages])

    return (
        <>
        { !(list.empty) && <div>
            <ul className="pagination justify-content-center">
                <li className={firstButtonClassName} key={-1}>
                    <button className="page-link" data-page={firstDataPage}>
                        <span>이전</span>
                    </button>
                </li>

               {_.range(0, list.totalPages - 1).map(
                page=>(
                    (page >= list.number - 5 && page <= list.number + 5) &&
                <li className={`page-item ${page === list.number ? 'active' : ''}`}
                    key={page}>
                    <button className="page-link" data-page={page}>{page}</button>
                </li>
                
               ))}

                <li className={lastButtonClassName} key={list.totalPages+1}>
                    <button className="page-link" data-page={lastDataPage}>
                        <span>다음</span>
                    </button>
                </li>
            </ul>
        </div>}
        </>
    )
}


function Create() {
    return (
    <div className="row my-3">
        <div className="col-6">
            <a href="/question/create" className="btn btn-primary">질문 등록하기</a>
            <a href="/?sortby=recent-answer" className="btn btn-secondary">최근 답변순</a>
            <a href="/?sortby=recent-comment" className="btn btn-secondary">최근 댓글순</a>
        </div>
    </div>
    )
}


function List() {
    const [list, setList] = useState({content:[]})
    const currentURL = new URL(window.location.href);

    const getList = async ()=>{
        const page = currentURL.searchParams.get('page')
        const kw = currentURL.searchParams.get('kw')
        const sortBy = currentURL.searchParams.get('sortby')
        const body = {
            page: page,
            kw: kw ,
            sortBy:sortBy 
          };

        const response = await fetch("api/api/v1/question-list", {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        })
        const data = await response.json();
        setList(data)
    }
    useEffect(()=>{
        getList()
    }, [])
    return (
        <div className="container my-3">
            <Paging list={list}/>
            <table className="table">
                <thead className="table-dark">
                <tr className="text-center">
                    <th>번호</th>
                    <th style={{width:"50%"}}>제목</th>
                    <th>글쓴이</th>
                    <th>작성일시</th>
                    <th>조회수</th>
                </tr>
                </thead>
                <tbody>
                {list.content.map((item, index)=>(
                    <tr className="text-center" key={index}>
                    <td>{list.totalElements - (list.number * list.size) - index}</td>
                    <td className="text-start">
                        <a href={`/question/detail/${item.id}`}>{item.title}</a>
                        {item.answerList.length > 0 && 
                        <span className="text-danger small ms-2">
                                {item.answerList.length}
                        </span>}
                    </td>
                    <td>
                        {item.author !== null && <span>{item.author.username}</span>}
                    </td>
                    <td>{item.createDate}</td>
                    <td>{item.view}</td>

                </tr>
                )) }
                </tbody>
            </table>
            <Create/>
            <SearchBar list={list}/>
        </div>
    )
}

export default List