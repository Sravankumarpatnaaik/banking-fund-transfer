</main>
<footer class="site-footer">
    <p>Meridian is a demonstration ledger for learning JDBC transactions. It is not a licensed bank and it does not move real money. Times are shown in India Standard Time.</p>
</footer>
<script>
document.querySelectorAll("form[data-once]").forEach(function (form) {
    form.addEventListener("submit", function (event) {
        if (form.dataset.locked === "1") {
            event.preventDefault();
            return;
        }
        form.dataset.locked = "1";
    });
});
</script>
</body>
</html>
