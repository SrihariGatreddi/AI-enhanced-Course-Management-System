document.addEventListener('DOMContentLoaded', () => {
    const navLinks = document.querySelectorAll('.sidebar-nav .nav-link');
    const contentSections = document.querySelectorAll('.main-content .content-section');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            // Get the target content ID from the link's data attribute
            const targetId = link.getAttribute('data-target');
            const targetSection = document.getElementById(targetId);

            // Remove 'active' class from all links and sections
            navLinks.forEach(navLink => navLink.classList.remove('active'));
            contentSections.forEach(section => section.classList.remove('active'));

            // Add 'active' class to the clicked link and its target section
            link.classList.add('active');
            if (targetSection) {
                targetSection.classList.add('active');
            }
        });
    });
});

