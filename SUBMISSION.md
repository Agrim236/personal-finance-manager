# Assignment Submission Guide

## 1. GitHub repository (done)

**Public repo:** https://github.com/Agrim236/personal-finance-manager

Code is on branch `main`. To push future changes:

```bash
git add .
git commit -m "Your message"
git push origin main
```

## 2. Deploy on Render

1. Log in to [Render](https://render.com).
2. **New → Web Service** → connect your GitHub repo.
3. Runtime: **Docker** (uses included `Dockerfile`).
4. Environment variable: `SPRING_PROFILES_ACTIVE` = `render`
5. Deploy and copy the service URL (e.g. `https://personal-finance-manager-xxxx.onrender.com`).

## 3. Run E2E tests against deployment

```bash
bash financial_manager_tests.sh https://YOUR-SERVICE.onrender.com/api
```

Expected output:

```text
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
🎉 ALL TESTS PASSED! 🎉
```

## 4. Email submission

Send to **Fauzia Khan**:

- GitHub repository link: `https://github.com/Agrim236/personal-finance-manager`
- Live API URL: `https://YOUR-SERVICE.onrender.com/api`
- Screenshot of the test script showing 86/86 passed

## 5. Local verification before submit

```bash
./mvnw verify
./mvnw spring-boot:run
# In another terminal:
bash financial_manager_tests.sh http://localhost:5003/api
```
