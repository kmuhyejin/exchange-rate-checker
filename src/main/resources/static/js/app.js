// ===== 통화 이름 매핑 =====
const CURRENCY_NAMES = {
    USD: '미국 달러',
    EUR: '유로',
    JPY: '일본 엔',
    GBP: '영국 파운드',
    CNY: '중국 위안',
    KRW: '한국 원',
    AUD: '호주 달러',
    CAD: '캐나다 달러',
    CHF: '스위스 프랑',
    HKD: '홍콩 달러',
    SGD: '싱가포르 달러',
    SEK: '스웨덴 크로나',
    NOK: '노르웨이 크로네',
    DKK: '덴마크 크로네',
};

// ===== 스피너 =====
function showSpinner() {
    document.getElementById('spinner').classList.remove('hidden');
}
function hideSpinner() {
    document.getElementById('spinner').classList.add('hidden');
}

// ===== 환율 조회 =====
async function fetchRates() {
    const base = document.getElementById('baseCurrency').value;
    showSpinner();

    try {
        const res = await fetch(`/api/exchange/rates?base=${base}`);
        if (!res.ok) throw new Error('환율 조회 실패');
        const data = await res.json();
        renderRates(data);
    } catch (err) {
        alert('환율 정보를 가져오는 데 실패했습니다.\n' + err.message);
    } finally {
        hideSpinner();
    }
}

function renderRates(data) {
    const resultDiv = document.getElementById('ratesResult');
    const grid = document.getElementById('ratesGrid');
    const lastUpdatedEl = document.getElementById('lastUpdated');
    const nextUpdateEl = document.getElementById('nextUpdate');

    lastUpdatedEl.textContent = `🕐 마지막 업데이트: ${data.lastUpdated || '-'}`;
    nextUpdateEl.textContent = `⏭ 다음 업데이트: ${data.nextUpdate || '-'}`;

    grid.innerHTML = '';
    const rates = data.rates;

    Object.entries(rates)
        .sort(([a], [b]) => a.localeCompare(b))
        .forEach(([code, rate]) => {
            if (code === data.baseCurrency) return; // 기준 통화 제외

            const card = document.createElement('div');
            card.className = 'rate-card';
            card.innerHTML = `
                <div class="currency-code">${code}</div>
                <div class="rate-value">${formatRate(rate)}</div>
                <div class="currency-name">${CURRENCY_NAMES[code] || ''}</div>
            `;
            grid.appendChild(card);
        });

    resultDiv.classList.remove('hidden');
}

function formatRate(rate) {
    if (rate >= 100) return rate.toLocaleString('ko-KR', { maximumFractionDigits: 2 });
    if (rate >= 1) return rate.toFixed(4);
    return rate.toFixed(6);
}

// ===== 통화 변환 =====
async function convertCurrency() {
    const from = document.getElementById('fromCurrency').value;
    const to = document.getElementById('toCurrency').value;
    const amount = parseFloat(document.getElementById('amount').value);

    if (!amount || amount <= 0) {
        alert('올바른 금액을 입력해주세요.');
        return;
    }
    if (from === to) {
        alert('변환 전후 통화가 동일합니다.');
        return;
    }

    showSpinner();

    try {
        const res = await fetch(`/api/exchange/convert?from=${from}&to=${to}&amount=${amount}`);
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || '변환 실패');
        }
        const data = await res.json();
        renderConvertResult(data);
        fetchHistory(); // 변환 후 이력 자동 갱신
    } catch (err) {
        alert('통화 변환에 실패했습니다.\n' + err.message);
    } finally {
        hideSpinner();
    }
}

function renderConvertResult(data) {
    const resultDiv = document.getElementById('convertResult');
    const output = document.getElementById('convertOutput');
    const rateEl = document.getElementById('convertRate');

    const formattedAmount = data.amount.toLocaleString('ko-KR');
    const formattedResult = data.result.toLocaleString('ko-KR', { maximumFractionDigits: 2 });

    output.textContent = `${formattedAmount} ${data.from}  =  ${formattedResult} ${data.to}`;
    rateEl.textContent = `1 ${data.from} = ${data.rate} ${data.to}  ·  ${data.convertedAt}`;

    resultDiv.classList.remove('hidden');
}

// ===== 조회 이력 =====
async function fetchHistory() {
    try {
        const res = await fetch('/api/exchange/history');
        if (!res.ok) throw new Error('이력 조회 실패');
        const data = await res.json();
        renderHistory(data);
    } catch (err) {
        console.error('이력 조회 실패:', err);
    }
}

function renderHistory(histories) {
    const container = document.getElementById('historyResult');

    if (!histories || histories.length === 0) {
        container.innerHTML = '<p class="empty-msg">조회 이력이 없습니다.</p>';
        return;
    }

    const table = `
        <table class="history-table">
            <thead>
                <tr>
                    <th>기준 통화</th>
                    <th>대상 통화</th>
                    <th>입력 금액</th>
                    <th>변환 금액</th>
                    <th>환율</th>
                    <th>조회 시각</th>
                </tr>
            </thead>
            <tbody>
                ${histories.map(h => `
                    <tr>
                        <td><strong>${h.baseCurrency}</strong></td>
                        <td><strong>${h.targetCurrency}</strong></td>
                        <td>${h.inputAmount ? h.inputAmount.toLocaleString('ko-KR') : '-'}</td>
                        <td>${h.convertedAmount ? h.convertedAmount.toLocaleString('ko-KR', { maximumFractionDigits: 2 }) : '-'}</td>
                        <td>${h.rate}</td>
                        <td>${formatDateTime(h.queriedAt)}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    container.innerHTML = table;
}

function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '-';
    const dt = new Date(dateTimeStr);
    if (isNaN(dt)) return dateTimeStr;
    return dt.toLocaleString('ko-KR', {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit', second: '2-digit'
    });
}

// ===== 페이지 로드 시 자동 조회 =====
document.addEventListener('DOMContentLoaded', () => {
    fetchRates();
});
